package com.vodolazskiy.forecastapplication

import android.app.Activity
import android.app.Application
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import com.vodolazskiy.forecastapplication.core.di.DaggerAppComponent
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

//todo move it from here
private const val APP_CENTER_KEY = "8ce556ab-194a-48b0-a468-f685104bd443"

class ForecastApp : Application(), HasActivityInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    override fun onCreate() {
        super.onCreate()

        AppCenter.start(this, APP_CENTER_KEY, Analytics::class.java, Crashes::class.java)

        val component = DaggerAppComponent
            .builder()
            .context(this)
            .build()
        component.inject(this)
    }

    override fun activityInjector(): AndroidInjector<Activity> = dispatchingAndroidInjector
}
