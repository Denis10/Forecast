package com.vodolazskiy.forecastapplication.presentation.forecastlist

import android.util.Log
import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vodolazskiy.forecastapplication.core.di.modules.viewmodel.ViewModelKey
import com.vodolazskiy.forecastapplication.core.livedata.toSingleEvent
import com.vodolazskiy.forecastapplication.domain.ForecastUsecase
import com.vodolazskiy.forecastapplication.domain.entity.ForecastItem
import com.vodolazskiy.forecastapplication.presentation.base.BaseViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

@Module
interface ForecastViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(ForecastViewModel::class)
    fun provideForecastViewModel(viewModel: ForecastViewModel): ViewModel
}

class ForecastViewModel @Inject constructor(
        private val forecastUsecase: ForecastUsecase
) : BaseViewModel() {

    private val forecastsInternal = MutableLiveData<List<ForecastItem>>()
    val forecasts: LiveData<List<ForecastItem>> = forecastsInternal
    @VisibleForTesting
    val isLoadingInternal =
            MutableLiveData<Boolean>().apply { value = false }
    val isLoading: LiveData<Boolean> = isLoadingInternal
    private val errorInternal = MutableLiveData<Throwable>()
    val error: LiveData<Throwable> = errorInternal.toSingleEvent()

    private val exceptionHandler: CoroutineContext = CoroutineExceptionHandler { _, throwable ->
        Log.e("ForecastViewModel", "", throwable)
        isLoadingInternal.postValue(false)
        errorInternal.postValue(throwable)
    }

    fun getForecast() {
        if (forecastsInternal.value == null) {
            loadForecast()
        } else {
            //receive forecast from LiveData
        }
    }

    fun updateForecast() = loadForecast()

    private fun loadForecast() {
        if (isLoadingInternal.value == true) return
        isLoadingInternal.value = true
        viewModelScope.launch(Dispatchers.Main + exceptionHandler) {
            val task = async(backgroundContext) {
                forecastUsecase.getForecast()
            }
            val result = task.await()
            forecastsInternal.value = result.forecasts
            isLoadingInternal.value = false
            Log.d("ForecastViewModel", "list after await = $result")
        }
    }
}
