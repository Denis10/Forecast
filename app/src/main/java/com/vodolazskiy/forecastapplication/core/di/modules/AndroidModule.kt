package com.vodolazskiy.forecastapplication.core.di.modules

import dagger.Module
import dagger.android.support.AndroidSupportInjectionModule

@Module(includes = [
    AndroidSupportInjectionModule::class,
    ForecastScreenModule::class
])
interface AndroidModule
