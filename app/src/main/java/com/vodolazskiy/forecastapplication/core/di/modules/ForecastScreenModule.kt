package com.vodolazskiy.forecastapplication.core.di.modules

import androidx.lifecycle.ViewModel
import com.vodolazskiy.forecastapplication.core.di.modules.viewmodel.ViewModelFactoryModule
import com.vodolazskiy.forecastapplication.core.di.modules.viewmodel.ViewModelKey
import com.vodolazskiy.forecastapplication.core.di.scopes.ActivityScope
import com.vodolazskiy.forecastapplication.presentation.ForecastViewModel
import com.vodolazskiy.forecastapplication.presentation.MainActivity
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module(includes = [
    ViewModelFactoryModule::class,
    ForecastScreenModule.ForecastViewModelModule::class
])
interface ForecastScreenModule {

    @ActivityScope
    @ContributesAndroidInjector
    fun provideMainActivity(): MainActivity

    @Module
    abstract class ForecastViewModelModule {
        @Binds
        @IntoMap
        @ViewModelKey(ForecastViewModel::class)
        internal abstract fun provideForecastViewModel(viewModel: ForecastViewModel): ViewModel
    }
}
