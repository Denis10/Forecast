package com.vodolazskiy.forecastapplication.core.di.modules

import com.vodolazskiy.forecastapplication.domain.CurrentLocationService
import com.vodolazskiy.forecastapplication.domain.CurrentLocationServiceImpl
import com.vodolazskiy.forecastapplication.domain.ForecastService
import com.vodolazskiy.forecastapplication.domain.ForecastServiceImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module(includes = [
    NetworkModule::class
])
interface ServiceModule {

    @Singleton
    @Binds
    fun provideLocationService(service: CurrentLocationServiceImpl): CurrentLocationService

    @Singleton
    @Binds
    fun provideForecastService(service: ForecastServiceImpl): ForecastService
}
