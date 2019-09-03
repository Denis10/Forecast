package com.vodolazskiy.forecastapplication.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

private const val PERMISSIONS_REQUEST_LOCATION = 102
private const val LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION
private const val PERMISSION_FLOW_IN_PROGRESS = "PERMISSION_FLOW_IN_PROGRESS"

class LocationPermissionDelegate(
    private val activityAction:()-> FragmentActivity,
    private val successAction :()-> Unit,
    private val failureAction :()-> Unit,
    private val beforeDialogAction :()-> Unit
): PermissionDialog.PermissionCallback {

    private var permissionFlowInProgress = false

    fun restore(savedInstanceState: Bundle){
        permissionFlowInProgress = savedInstanceState.getBoolean(PERMISSION_FLOW_IN_PROGRESS)
    }

    fun save(outState: Bundle){
        outState.putBoolean(PERMISSION_FLOW_IN_PROGRESS, permissionFlowInProgress)
    }

    fun isLocationPermissionGranted(): Boolean {

        fun hasLocationPermission(): Boolean =
            ContextCompat.checkSelfPermission(activityAction(), LOCATION_PERMISSION) ==
                    PackageManager.PERMISSION_GRANTED

        return if (!hasLocationPermission()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activityAction(), LOCATION_PERMISSION)) {
                beforeDialogAction()
                if (!permissionFlowInProgress) {
                    permissionFlowInProgress = true
                    PermissionDialog.newInstance()
                        .show(activityAction().supportFragmentManager, PermissionDialog::class.java.name)
                }
                false
            } else {
                if (!permissionFlowInProgress) {
                    requestLocationPermission()
                }
                false
            }
        } else {
            true
        }
    }

    fun requestLocationPermission() {
        permissionFlowInProgress = true
        ActivityCompat.requestPermissions(activityAction(), arrayOf(LOCATION_PERMISSION), PERMISSIONS_REQUEST_LOCATION)
    }

    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                   grantResults: IntArray): Boolean {
        if (requestCode == PERMISSIONS_REQUEST_LOCATION){
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                successAction.invoke()
            } else {
                failureAction.invoke()
            }

            permissionFlowInProgress = false

            return true
        }

        return false
    }

    /*
    * PermissionCallback
    */

    override fun onLocationGranted() = requestLocationPermission()

    override fun onLocationDenied() {
        failureAction.invoke()
        permissionFlowInProgress = false
    }
}