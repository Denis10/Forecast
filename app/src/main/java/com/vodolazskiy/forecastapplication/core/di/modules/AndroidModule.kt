package com.vodolazskiy.forecastapplication.core.di.modules

import com.vodolazskiy.forecastapplication.core.di.scopes.ActivityScope
import com.vodolazskiy.forecastapplication.presentation.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.android.support.AndroidSupportInjectionModule

@Module(includes = [(AndroidSupportInjectionModule::class)])
interface AndroidModule {

    @ActivityScope
    @ContributesAndroidInjector
    fun provideMainActivity(): MainActivity

}
