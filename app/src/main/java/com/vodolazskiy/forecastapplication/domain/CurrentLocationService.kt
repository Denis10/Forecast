package com.vodolazskiy.forecastapplication.domain

import kotlinx.coroutines.channels.Channel
import java.util.concurrent.atomic.AtomicBoolean

interface CurrentLocationService {

    suspend fun getCurrentLocation(): Pair<Double, Double>

    suspend fun getLastKnownLocation(): Pair<Double, Double>?

    suspend fun listenLocations(locationChannel: Channel<Pair<Double, Double>>)

    suspend fun stopListening()
}
