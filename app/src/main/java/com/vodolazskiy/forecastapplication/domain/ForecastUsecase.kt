package com.vodolazskiy.forecastapplication.domain

import com.vodolazskiy.forecastapplication.domain.entity.Forecast
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.flatMap
import javax.inject.Inject

interface ForecastUsecase {

    suspend fun getForecast(): Forecast

    suspend fun listenForecasts(forecastsChannel: Channel<Forecast>)
}

class ForecastUsecaseImpl @Inject constructor(
    private val locationService: CurrentLocationService,
    private val forecastService: ForecastService
) : ForecastUsecase {

    //todo close it?
    private val locationChannel = Channel<Pair<Double, Double>>(capacity = Channel.RENDEZVOUS)

    override suspend fun getForecast(): Forecast {
        return locationService.getCurrentLocation()
            .let {
                forecastService.fiveDaysForecast(it.first, it.second)
            }
    }

    override suspend fun listenForecasts(forecastsChannel: Channel<Forecast>) {
        locationService.listenLocations(locationChannel)
        val list = locationChannel.receive()
            .let {
                forecastService.fiveDaysForecast(it.first, it.second)
            }
        forecastsChannel.send(list)
    }
}
