package com.vodolazskiy.forecastapplication.domain

interface CurrentLocationService {

    suspend fun getCurrentLocation(): Pair<Double, Double>
}
