package com.vodolazskiy.forecastapplication.presentation.forecastdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.vodolazskiy.forecastapplication.R
import com.vodolazskiy.forecastapplication.core.di.modules.viewmodel.injectViewModel
import com.vodolazskiy.forecastapplication.presentation.base.BaseFragment
import kotlinx.android.synthetic.main.item_forecast.*
import java.text.SimpleDateFormat
import javax.inject.Inject

class ForecastDetailsFragment : BaseFragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var forecastViewModel: ForecastDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        forecastViewModel = injectViewModel(viewModelFactory)

        val args: ForecastDetailsFragmentArgs by navArgs()
        forecastViewModel.setData(args.forecastItem)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_forecast_details, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        forecastViewModel.forecast.observe(this, Observer {
            txtDateValue.text = SimpleDateFormat.getDateTimeInstance().format(it.date)
            txtTemperatureValue.text = it.mainForecast.temp.toInt().toString()
        })
    }
}