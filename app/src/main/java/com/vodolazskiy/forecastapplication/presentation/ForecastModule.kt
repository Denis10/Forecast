package com.vodolazskiy.forecastapplication.presentation

import com.vodolazskiy.forecastapplication.core.di.modules.viewmodel.ViewModelFactoryModule
import com.vodolazskiy.forecastapplication.core.di.scopes.ActivityScope
import com.vodolazskiy.forecastapplication.core.di.scopes.FragmentScope
import com.vodolazskiy.forecastapplication.presentation.forecastdetails.ForecastDetailsFragment
import com.vodolazskiy.forecastapplication.presentation.forecastdetails.ForecastDetailsViewModel
import com.vodolazskiy.forecastapplication.presentation.forecastdetails.ForecastDetailsViewModelModule
import com.vodolazskiy.forecastapplication.presentation.forecastlist.ForecastListFragment
import com.vodolazskiy.forecastapplication.presentation.forecastlist.ForecastViewModelModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module(
    includes = [
        ViewModelFactoryModule::class
    ]
)
interface ForecastModule {

    @ActivityScope
    @ContributesAndroidInjector
    fun provideMainActivity(): MainActivity

    @FragmentScope
    @ContributesAndroidInjector(modules = [ForecastViewModelModule::class])
    fun bindForecastListFragment(): ForecastListFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [ForecastDetailsViewModelModule::class])
    fun bindForecastDetailsFragment(): ForecastDetailsFragment
}
