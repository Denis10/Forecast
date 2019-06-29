package com.vodolazskiy.forecastapplication.service

import android.content.Context
import com.vodolazskiy.forecastapplication.R
import com.vodolazskiy.forecastapplication.domain.ForecastService
import com.vodolazskiy.forecastapplication.domain.entity.Forecast
import com.vodolazskiy.forecastapplication.domain.exceptions.ServiceError
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import javax.inject.Inject
import kotlin.coroutines.resumeWithException

private const val URL = "https://api.openweathermap.org/data/2.5/forecast"
private const val API_KEY_PARAM = "APPID"
private const val LATITUDE_PARAM = "lat"
private const val LONGITUDE_PARAM = "lon"


class ForecastServiceImpl @Inject constructor(
        context: Context,
        private val okHttpClient: OkHttpClient
) : ForecastService {

    private val apiKey = context.getString(R.string.forecast_key)

    override suspend fun fiveDaysForecast(lat: Double, lng: Double): Forecast {
        val urlBuilder = requireNotNull(HttpUrl.parse(URL)).newBuilder()

        urlBuilder.addQueryParameter(LATITUDE_PARAM, lat.toString())
        urlBuilder.addQueryParameter(LONGITUDE_PARAM, lng.toString())
        urlBuilder.addQueryParameter(API_KEY_PARAM, apiKey)
        val url = urlBuilder.build().toString()

        val request = Request.Builder()
                .url(url)
                .build()

        return suspendCancellableCoroutine { coroutine ->
            try {
                val response = okHttpClient.newCall(request).execute()
                if (response.isSuccessful) {
                    val jsonData = requireNotNull(response.body()).string()
                    val jsonObject = JSONObject(jsonData)
                    val domainForecast = ForecastParser.parse(jsonObject)
                    coroutine.resumeWith(Result.success(domainForecast))
                } else {
                    coroutine.resumeWithException(ServiceError.ApiError(reason = response.toString()))
                }
            } catch (e: Exception) {
                coroutine.resumeWithException(ServiceError.ApiError(e))
            }
        }
    }
}
