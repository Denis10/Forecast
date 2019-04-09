package com.vodolazskiy.forecastapplication.presentation

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.vodolazskiy.forecastapplication.domain.ForecastUsecase
import com.vodolazskiy.forecastapplication.domain.entity.ForecastItem
import com.vodolazskiy.forecastapplication.presentation.base.BaseViewModel
import kotlinx.coroutines.*
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class ForecastViewModel @Inject constructor(
    private val forecastUsecase: ForecastUsecase
) : BaseViewModel() {

    val forecasts = MutableLiveData<List<ForecastItem>>()

    val isLoading = MutableLiveData<Boolean>().apply { value = false }

    // dispatches execution into Android main thread
    private val uiDispatcher: CoroutineDispatcher = Dispatchers.Main
    // represent a pool of shared threads as coroutine dispatcher
    private val bgDispatcher: CoroutineDispatcher = Dispatchers.IO

    private val exceptionHandler: CoroutineContext = CoroutineExceptionHandler { _, throwable ->
        Log.e("ForecastViewModel", "", throwable)
    }

    init {
        getForecast()
    }

    fun getForecast() {
        isLoading.value = true
        Log.d("Denis", "ForecastViewModel start")
        viewModelScope.launch(uiDispatcher + exceptionHandler) {
            val task = async(bgDispatcher) {
                forecastUsecase.getForecast()
            }
            val result = task.await()
            forecasts.value = result.forecasts
            isLoading.value = false
            Log.d("ForecastViewModel", "list after await = $result")
        }
    }
}
