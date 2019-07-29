package com.vodolazskiy.forecastapplication.core.di.modules

import androidx.lifecycle.ViewModel
import com.vodolazskiy.forecastapplication.core.di.modules.viewmodel.ViewModelFactoryModule
import com.vodolazskiy.forecastapplication.core.di.modules.viewmodel.ViewModelKey
import com.vodolazskiy.forecastapplication.core.di.scopes.ActivityScope
import com.vodolazskiy.forecastapplication.core.di.scopes.FragmentScope
import com.vodolazskiy.forecastapplication.presentation.forecastlist.ForecastViewModel
import com.vodolazskiy.forecastapplication.presentation.MainActivity
import com.vodolazskiy.forecastapplication.presentation.forecastlist.ForecastListFragment
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module(
    includes = [
        ViewModelFactoryModule::class,
        ForecastScreenModule.ForecastViewModelModule::class
    ]
)
interface ForecastScreenModule {

    @ActivityScope
    @ContributesAndroidInjector
    fun provideMainActivity(): MainActivity

    @FragmentScope
    @ContributesAndroidInjector
    fun bindforecastListFragment(): ForecastListFragment

    @Module
    abstract class ForecastViewModelModule {
        @Binds
        @IntoMap
        @ViewModelKey(ForecastViewModel::class)
        internal abstract fun provideForecastViewModel(viewModel: ForecastViewModel): ViewModel
    }
}
