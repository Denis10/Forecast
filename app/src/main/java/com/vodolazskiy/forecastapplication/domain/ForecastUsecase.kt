package com.vodolazskiy.forecastapplication.domain

import com.vodolazskiy.forecastapplication.domain.entity.Forecast
import javax.inject.Inject

interface ForecastUsecase {

    suspend fun getForecast(): Forecast
}

class ForecastUsecaseImpl @Inject constructor(
    private val locationService: CurrentLocationService,
    private val forecastService: ForecastService
) : ForecastUsecase {

    override suspend fun getForecast(): Forecast {
        return locationService.getCurrentLocation()
            .let {
                forecastService.fiveDaysForecast(it.first, it.second)
            }
    }
}
