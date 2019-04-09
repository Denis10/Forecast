package com.vodolazskiy.forecastapplication.presentation

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.vodolazskiy.forecastapplication.R
import com.vodolazskiy.forecastapplication.core.di.modules.viewmodel.injectViewModel
import com.vodolazskiy.forecastapplication.presentation.base.BaseActivity
import com.vodolazskiy.forecastapplication.presentation.base.setRefreshLock
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : BaseActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var forecastViewModel: ForecastViewModel

    private val adapter = ForecastAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        forecastViewModel = injectViewModel(viewModelFactory)

        //todo permissions

        swipeRefreshLayout.setOnRefreshListener {
            forecastViewModel.getForecast()
        }

        rvForecasts.adapter = adapter
        val columns = resources.getInteger(R.integer.forecast_grid_columns)
        rvForecasts.layoutManager = StaggeredGridLayoutManager(columns, StaggeredGridLayoutManager.VERTICAL)

        swipeRefreshLayout.setRefreshLock {
            swipeRefreshLayout.isRefreshing.not()
        }

        forecastViewModel.forecasts.observe(this, Observer {
            adapter.submitList(it)
        })

        forecastViewModel.isLoading.observe(this, Observer {
            swipeRefreshLayout.post {
                swipeRefreshLayout?.isRefreshing = it
            }
        })
    }
}
