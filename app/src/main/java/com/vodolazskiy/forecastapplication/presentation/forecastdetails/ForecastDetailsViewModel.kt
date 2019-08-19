package com.vodolazskiy.forecastapplication.presentation.forecastdetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vodolazskiy.forecastapplication.core.di.modules.viewmodel.ViewModelKey
import com.vodolazskiy.forecastapplication.domain.entity.ForecastItem
import com.vodolazskiy.forecastapplication.presentation.base.BaseViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import javax.inject.Inject

@Module
interface ForecastDetailsViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(ForecastDetailsViewModel::class)
    fun provideForecastDetailsViewModel(viewModel: ForecastDetailsViewModel): ViewModel
}

class ForecastDetailsViewModel @Inject constructor() : BaseViewModel() {

    private val forecastInternal = MutableLiveData<ForecastItem>()
    val forecast: LiveData<ForecastItem> = forecastInternal

    fun setData(item: ForecastItem){
        forecastInternal.postValue(item)
    }
}