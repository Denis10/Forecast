package com.vodolazskiy.forecastapplication.presentation

import android.Manifest
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val PERMISSIONS_REQUEST_LOCATION = 102
private const val LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION
private const val PERMISSION_FLOW_IN_PROGRESS = "PERMISSION_FLOW_IN_PROGRESS"

class MainActivity : BaseActivity(), PermissionDialog.PermissionCallback {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var forecastViewModel: ForecastViewModel

    private var permissionFlowInProgress = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        permissionFlowInProgress =
            savedInstanceState?.getBoolean(PERMISSION_FLOW_IN_PROGRESS) ?: false

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
        if (permissionsManager.isPermissionGranted(LOCATION_PERMISSION)) {
            forecastViewModel.getForecast()
        } else {
            requestPermissionsWithRational()
        }

        launch {
            permissionsManager.listenForCode(PERMISSIONS_REQUEST_LOCATION).let {
                withContext(Dispatchers.Main) {
                    permissionFlowInProgress = false
                }
                when {
                    it.isAllGranted -> forecastViewModel.getForecast()
                    else -> withContext(Dispatchers.Main) {
                        showEmptyView(true)
                    }
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(PERMISSION_FLOW_IN_PROGRESS, permissionFlowInProgress)
        super.onSaveInstanceState(outState)
    }

    private fun requestPermissionsWithRational() {
        if (permissionsManager.shouldShowRequestPermissionRationale(LOCATION_PERMISSION)) {
            showExplanation()
        } else {
            requestPermissions()
        }
    }

    private fun showExplanation() {
        showEmptyView(true)
        if (!permissionFlowInProgress) {
            permissionFlowInProgress = true
            PermissionDialog.newInstance()
                .show(supportFragmentManager, PermissionDialog::class.java.name)
        }
    }

    private fun requestPermissions() {
        permissionFlowInProgress = true
        permissionsManager.requestPermissions(PERMISSIONS_REQUEST_LOCATION, LOCATION_PERMISSION)
    }

    private fun showEmptyView(show: Boolean) {
        emptyView.isVisible = show
        rvForecasts.isVisible = !show
    }

    /*
    * PermissionCallback
    */

    override fun onLocationGranted() {
        requestPermissions()
    }

    override fun onLocationDenied() {
        showEmptyView(true)
        permissionFlowInProgress = false
    }
}
