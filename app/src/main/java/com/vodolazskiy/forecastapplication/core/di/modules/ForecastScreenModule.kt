package com.vodolazskiy.forecastapplication.core.di.modules

import androidx.lifecycle.ViewModel
import com.vodolazskiy.forecastapplication.core.di.modules.viewmodel.ViewModelKey
import com.vodolazskiy.forecastapplication.presentation.ForecastViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module(includes = [
    ForecastScreenModule.ForecastViewModelModule::class
])
abstract class ForecastScreenModule {

    @Module
    abstract class ForecastViewModelModule {
        @Binds
        @IntoMap
        @ViewModelKey(ForecastViewModel::class)
        internal abstract fun provideForecastViewModel(viewModel: ForecastViewModel): ViewModel
    }
}
