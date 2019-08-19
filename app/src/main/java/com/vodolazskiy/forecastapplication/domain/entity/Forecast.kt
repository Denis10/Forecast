package com.vodolazskiy.forecastapplication.domain.entity

import java.io.Serializable
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
): Serializable

data class MainForecast(
    val temp: Double
): Serializable
