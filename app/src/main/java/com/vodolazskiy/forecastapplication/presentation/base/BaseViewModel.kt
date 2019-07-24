package com.vodolazskiy.forecastapplication.presentation.base

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModel : ViewModel() {

    var backgroundContext: CoroutineContext = Dispatchers.IO
        private set

    @ExperimentalCoroutinesApi
    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }

    @VisibleForTesting
    fun setTestContext(context: CoroutineContext) {
        backgroundContext = context
    }
}
