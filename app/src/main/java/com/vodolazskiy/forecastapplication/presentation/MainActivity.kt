package com.vodolazskiy.forecastapplication.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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

private const val PERMISSIONS_REQUEST_LOCATION = 102
private const val LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION

class MainActivity : BaseActivity(), PermissionDialog.PermissionCallback {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var forecastViewModel: ForecastViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        forecastViewModel = injectViewModel(viewModelFactory)

        val adapter = ForecastAdapter()
        rvForecasts.adapter = adapter
        val columns = resources.getInteger(R.integer.forecast_grid_columns)
        rvForecasts.layoutManager = StaggeredGridLayoutManager(columns, StaggeredGridLayoutManager.VERTICAL)

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
        if (isLocationPermissionGranted()) {
            forecastViewModel.getForecast()
        }
    }

    private fun showEmptyView(show: Boolean) {
        emptyView.isVisible = show
        rvForecasts.isVisible = !show
    }

    private fun isLocationPermissionGranted(): Boolean {

        fun hasLocationPermission(): Boolean =
            ContextCompat.checkSelfPermission(this, LOCATION_PERMISSION) ==
                PackageManager.PERMISSION_GRANTED

        return if (!hasLocationPermission()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, LOCATION_PERMISSION)) {
                showEmptyView(true)
                PermissionDialog.newInstance().show(supportFragmentManager, PermissionDialog::class.java.name)
                false
            } else {
                requestLocationPermission()
                false
            }
        } else {
            true
        }
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(LOCATION_PERMISSION), PERMISSIONS_REQUEST_LOCATION)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_LOCATION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    forecastViewModel.updateForecast()
                } else {
                    showEmptyView(true)
                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }

    /*
    * PermissionCallback
    */

    override fun onLocationGranted() = requestLocationPermission()

    override fun onLocationDenied() {
        showEmptyView(true)
    }
}
