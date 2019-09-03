package com.vodolazskiy.forecastapplication.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.vodolazskiy.forecastapplication.R
import com.vodolazskiy.forecastapplication.core.di.modules.viewmodel.injectViewModel
import com.vodolazskiy.forecastapplication.core.permissions.OnRequestPermissionsResultEvent
import com.vodolazskiy.forecastapplication.core.permissions.PermissionsManagerDelegate
import com.vodolazskiy.forecastapplication.presentation.base.BaseActivity
import com.vodolazskiy.forecastapplication.presentation.base.setRefreshLock
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

private const val LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION

class MainActivity : BaseActivity(), PermissionDialog.PermissionCallback {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var forecastViewModel: ForecastViewModel

    private val permissionsManager = PermissionsManagerDelegate { this@MainActivity }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
        if (hasLocationPermission()) {
            forecastViewModel.getForecast()
        } else {
            requestPermissions()
        }
    }

    private fun hasLocationPermission(): Boolean =
        ContextCompat.checkSelfPermission(this, LOCATION_PERMISSION) ==
                PackageManager.PERMISSION_GRANTED

    private fun requestPermissions() = launch {
        permissionsManager.requestPermissions(LOCATION_PERMISSION).let {
            when {
                it.isShouldShowRequestPermissionRationale -> {
                    withContext(Dispatchers.Main) {
                        showEmptyView(true)
                        PermissionDialog.newInstance()
                            .show(supportFragmentManager, PermissionDialog::class.java.name)
                    }
                }
                it.isAllGranted -> forecastViewModel.getForecast()
                else -> withContext(Dispatchers.Main) {
                    showEmptyView(true)
                }
            }
        }
    }

    private fun showEmptyView(show: Boolean) {
        emptyView.isVisible = show
        rvForecasts.isVisible = !show
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        launch {
            val shouldShowRequestPermissionRationale = BooleanArray(permissions.size) {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && shouldShowRequestPermissionRationale(
                    permissions[it]
                )
            }
            permissionsManager.permissionsObservable.send(
                OnRequestPermissionsResultEvent(
                    requestCode = requestCode,
                    permissions = permissions.toList(),
                    grantResults = grantResults.toList(),
                    shouldShowRequestPermissionRationale = shouldShowRequestPermissionRationale.toList()
                )
            )
        }
    }

    /*
    * PermissionCallback
    */

    override fun onLocationGranted() {
        requestPermissions()
    }

    override fun onLocationDenied() {
        showEmptyView(true)
    }
}
