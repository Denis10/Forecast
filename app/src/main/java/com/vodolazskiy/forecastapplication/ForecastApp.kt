package com.vodolazskiy.forecastapplication

import android.app.Activity
import android.app.Application
import com.vodolazskiy.forecastapplication.core.di.DI
import com.vodolazskiy.forecastapplication.core.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

class ForecastApp : Application(), HasActivityInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()

        val component = DaggerAppComponent
            .builder()
            .context(this)
            .build()
        component.inject(this)
        DI.init(component)
    }

    override fun activityInjector(): AndroidInjector<Activity> = dispatchingAndroidInjector
}
