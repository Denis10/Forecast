package com.vodolazskiy.forecastapplication.core.di

import android.content.Context
import com.vodolazskiy.forecastapplication.ForecastApp
import com.vodolazskiy.forecastapplication.core.di.modules.AndroidModule
import com.vodolazskiy.forecastapplication.core.di.modules.UsecaseModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [
    AndroidModule::class,
    UsecaseModule::class
])
interface AppComponent : AppGraph {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun context(context: Context): Builder

        fun build(): AppComponent
    }

    fun inject(app: ForecastApp)
}
