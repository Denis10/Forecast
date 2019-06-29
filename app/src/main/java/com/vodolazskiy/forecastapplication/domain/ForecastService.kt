package com.vodolazskiy.forecastapplication.domain

import com.vodolazskiy.forecastapplication.domain.entity.Forecast

interface ForecastService {

    suspend fun fiveDaysForecast(lat: Double, lng: Double): Forecast
}
