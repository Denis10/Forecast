package com.vodolazskiy.forecastapplication.core.di.modules

import com.vodolazskiy.forecastapplication.presentation.ForecastModule
import dagger.Module
import dagger.android.support.AndroidSupportInjectionModule

@Module(includes = [
    AndroidSupportInjectionModule::class,
    ForecastModule::class
])
interface AndroidModule
