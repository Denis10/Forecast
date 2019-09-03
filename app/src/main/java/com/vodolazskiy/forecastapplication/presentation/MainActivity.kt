package com.vodolazskiy.forecastapplication.presentation

import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.vodolazskiy.forecastapplication.R
import com.vodolazskiy.forecastapplication.core.di.modules.viewmodel.injectViewModel
import com.vodolazskiy.forecastapplication.presentation.base.BaseActivity
import com.vodolazskiy.forecastapplication.presentation.base.setRefreshLock
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject


class MainActivity : BaseActivity(), PermissionDialog.PermissionCallback {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var forecastViewModel: ForecastViewModel

    private val permissionDelegate = LocationPermissionDelegate(
        activityAction = { this },
        successAction = { forecastViewModel.updateForecast() },
        failureAction = { showEmptyView(true) },
        beforeDialogAction = { showEmptyView(true) }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        savedInstanceState?.run { permissionDelegate.restore(this) }

        forecastViewModel = injectViewModel(viewModelFactory)

        val adapter = ForecastAdapter()
        rvForecasts.adapter = adapter
        val columns = resources.getInteger(R.integer.forecast_grid_columns)
        rvForecasts.layoutManager =
            StaggeredGridLayoutManager(columns, StaggeredGridLayoutManager.VERTICAL)

        swipeRefreshLayout.setOnRefreshListener {
            forecastViewModel.updateForecast()
        }
        swipeRefreshLayout.setRefreshLock {
            swipeRefreshLayout.isRefreshing.not()
        }

        forecastViewModel.forecasts.observe(this, Observer {
            adapter.submitList(it)
            showEmptyView(it.isEmpty())
        })
        forecastViewModel.isLoading.observe(this, Observer {
            swipeRefreshLayout.post {
                swipeRefreshLayout?.isRefreshing = it
            }
        })
        forecastViewModel.error.observe(this, Observer {
            Toast.makeText(this, getString(R.string.something_wrong), Toast.LENGTH_SHORT).show()
        })
        if (permissionDelegate.isLocationPermissionGranted()) {
            forecastViewModel.getForecast()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        permissionDelegate.save(outState)
        super.onSaveInstanceState(outState)
    }

    private fun showEmptyView(show: Boolean) {
        emptyView.isVisible = show
        rvForecasts.isVisible = !show
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        if (permissionDelegate.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            return
        } else {
            // Ignore all other requests.
        }
    }

    /*
    * PermissionCallback
    */

    override fun onLocationGranted() = permissionDelegate.onLocationGranted()

    override fun onLocationDenied() = permissionDelegate.onLocationDenied()
}
