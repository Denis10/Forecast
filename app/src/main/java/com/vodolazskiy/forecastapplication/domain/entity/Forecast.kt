package com.vodolazskiy.forecastapplication.domain.entity

import java.util.*

data class Forecast(
    val city: City,
    val forecasts: List<ForecastItem>)

data class City(
    val id: String,
    val name: String
)

data class ForecastItem(
    val date: Date,
    val mainForecast: MainForecast
)

data class MainForecast(
    val temp: Double
)
