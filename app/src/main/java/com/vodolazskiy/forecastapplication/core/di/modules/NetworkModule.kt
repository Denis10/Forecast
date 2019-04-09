package com.vodolazskiy.forecastapplication.core.di.modules

import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
class NetworkModule {

    @Singleton
    @Provides
    fun provideOkHttp(): OkHttpClient = OkHttpClient()
}
