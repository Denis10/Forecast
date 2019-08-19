package com.vodolazskiy.forecastapplication.service

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import com.vodolazskiy.forecastapplication.domain.CurrentLocationService
import com.vodolazskiy.forecastapplication.domain.exceptions.ServiceError
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import kotlin.coroutines.resumeWithException

private const val UPDATE_INTERVAL = 5000L
private const val FASTEST_INTERVAL = 5000L
private const val LOCATION_EXPIRE_TIME: Long = 3600 * 1000
private const val TIMEOUT = 60_000L

class CurrentLocationServiceImpl @Inject constructor(private val context: Context) : CurrentLocationService {

    private val isListening: AtomicBoolean = AtomicBoolean()
    private var listeningCallback: LocationCallback? = null

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): Pair<Double, Double> {
        if (!hasLocationPermission(context)) {
            throw ServiceError.NoLocationException()
        }
        val location = runWithTimeout {
            getLastLocation()
                ?.let {
                    if (Calendar.getInstance().time.time - it.time > LOCATION_EXPIRE_TIME) {
                        requestLocation()
                    } else {
                        it
                    }
                } ?: requestLocation()
        }

        return location.latitude to location.longitude
    }

    private suspend fun <T> runWithTimeout(block: suspend CoroutineScope.() -> T): T {
        return try {
            withTimeout(TIMEOUT) {
                block.invoke(this)
            }
        } catch (e: TimeoutCancellationException) {
            throw ServiceError.NoLocationException(e)
        }
    }

    private fun hasLocationPermission(context: Context) =
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    private suspend fun getLastLocation(): Location? {
        try {
            return suspendCancellableCoroutine { coroutine ->
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location: Location? ->
                        coroutine.resumeWith(Result.success(location))
                    }
                    .addOnFailureListener {
                        coroutine.resumeWithException(it)
                    }
            }
        } catch (e: Exception) {
            throw ServiceError.NoLocationException(e)
        }
    }

    override suspend fun getLastKnownLocation(): Pair<Double, Double>? {
        val location = getLastLocation()
        return if (location == null) {
            null
        } else {
            location.latitude to location.longitude
        }
    }

    private suspend fun requestLocation(): Location {
        try {
            return suspendCancellableCoroutine { coroutine ->
                val locationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult?) {
                        fusedLocationClient.removeLocationUpdates(this)
                        locationResult ?: throw ServiceError.NoLocationException()
                        if (locationResult.locations.isEmpty()) throw ServiceError.NoLocationException()

                        coroutine.resumeWith(Result.success(locationResult.locations[0]))
                    }
                }
                val request = LocationRequest()
                request.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                request.interval = UPDATE_INTERVAL
                request.fastestInterval = FASTEST_INTERVAL
                fusedLocationClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper())
                isListening.set(true)
            }
        } catch (e: Exception) {
            throw ServiceError.NoLocationException(e)
        }
    }

    //todo register channels
    @ExperimentalCoroutinesApi
    @SuppressLint("MissingPermission")
    override suspend fun listenLocations(locationChannel: Channel<Pair<Double, Double>>) {
        if (!hasLocationPermission(context)) {
            throw ServiceError.NoLocationException()
        }
        if (isListening.get()){
            getLastLocation()?.let {
                locationChannel.offer(it.latitude to it. longitude)
            }
        } else {
            listeningCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult?) {
                    locationResult ?: throw ServiceError.NoLocationException()
                    if (locationResult.locations.isEmpty()) throw ServiceError.NoLocationException()

                    if (!locationChannel.isClosedForSend) {
                        val location = locationResult.locations[0]
                        locationChannel.offer(location.latitude to location.longitude)
                    }
                }
            }
            val request = LocationRequest()
            request.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            request.interval = UPDATE_INTERVAL
            request.fastestInterval = FASTEST_INTERVAL
            fusedLocationClient.requestLocationUpdates(request, listeningCallback, Looper.getMainLooper())
            isListening.set(true)
        }
    }

    override suspend fun stopListening() {
        listeningCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
        }
    }
}
