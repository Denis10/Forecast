package com.vodolazskiy.forecastapplication.presentation.forecastdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.vodolazskiy.forecastapplication.R
import com.vodolazskiy.forecastapplication.core.di.modules.viewmodel.injectViewModel
import com.vodolazskiy.forecastapplication.presentation.base.BaseFragment
import javax.inject.Inject

class ForecastDetailsFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var forecastViewModel: ForecastDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        forecastViewModel = injectViewModel(viewModelFactory)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_forecast_details, container, false)
}