package com.vodolazskiy.forecastapplication.presentation.forecastlist

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.vodolazskiy.forecastapplication.R
import com.vodolazskiy.forecastapplication.core.di.modules.viewmodel.injectViewModel
import com.vodolazskiy.forecastapplication.presentation.base.BaseFragment
import com.vodolazskiy.forecastapplication.presentation.base.setRefreshLock
import kotlinx.android.synthetic.main.fragment_forecast_list.*
import javax.inject.Inject

private const val PERMISSIONS_REQUEST_LOCATION = 102
private const val LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION

class ForecastListFragment : BaseFragment(), PermissionDialog.PermissionCallback {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var forecastViewModel: ForecastViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        forecastViewModel = injectViewModel(viewModelFactory)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_forecast_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ForecastAdapter {
            findNavController().navigate(ForecastListFragmentDirections.toDetails(it))
        }
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
            Toast.makeText(requireContext(), getString(R.string.something_wrong), Toast.LENGTH_SHORT).show()
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
            ContextCompat.checkSelfPermission(requireActivity(), LOCATION_PERMISSION) ==
                    PackageManager.PERMISSION_GRANTED

        return if (!hasLocationPermission()) {
            if (shouldShowRequestPermissionRationale(LOCATION_PERMISSION)) {
                showEmptyView(true)
                PermissionDialog.newInstance(this)
                    .show(requireFragmentManager(), PermissionDialog::class.java.name)
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
        requestPermissions(arrayOf(LOCATION_PERMISSION), PERMISSIONS_REQUEST_LOCATION)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
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