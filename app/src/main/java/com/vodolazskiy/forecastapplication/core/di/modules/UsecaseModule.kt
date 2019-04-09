package com.vodolazskiy.forecastapplication.core.di.modules

import com.vodolazskiy.forecastapplication.domain.ForecastUsecase
import com.vodolazskiy.forecastapplication.domain.ForecastUsecaseImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface UsecaseModule {

    @Singleton
    @Binds
    fun provideForecast(login: ForecastUsecaseImpl): ForecastUsecase
}
