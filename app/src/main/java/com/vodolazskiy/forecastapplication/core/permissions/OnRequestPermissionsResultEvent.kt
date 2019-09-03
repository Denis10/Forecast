@file:Suppress("MemberVisibilityCanBePrivate")

package com.vodolazskiy.forecastapplication.core.permissions

import android.content.pm.PackageManager

/**
 * Raw data from [androidx.fragment.app.FragmentActivity.onRequestPermissionsResult] or
 * [androidx.fragment.app.Fragment.onRequestPermissionsResult]
 *
 * @param requestCode - permissions request code
 * @param permissions - list of requested permissions
 * @param grantResults - the grant results for the corresponding permissions
 * @param shouldShowRequestPermissionRationale - says whether you should show UI with rationale
 * for every requesting a permission
 * */
data class OnRequestPermissionsResultEvent(
        val requestCode: Int,
        val permissions: List<String>,
        val grantResults: List<Int>,
        val shouldShowRequestPermissionRationale: List<Boolean>) {

    /**
     * Returns true if all permissions ara granted
     * */
    val isAllGranted: Boolean get() = grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }

    /**
     * Returns true if specific permission is granted
     *
     * @param permission - permission to check
     * */
    fun isGranted(permission: String): Boolean = grantResults[permissions.indexOf(permission)] == PackageManager.PERMISSION_GRANTED

    /**
     * Returns true if specific permission needs explanation
     *
     * @param permission - permission to check
     * */
    fun isShouldShowRequestPermissionRationale(permission: String): Boolean = shouldShowRequestPermissionRationale[permissions.indexOf(permission)]

    /**
     * Returns true if any permission needs explanation
     */
    val isShouldShowRequestPermissionRationale: Boolean get() = shouldShowRequestPermissionRationale.any { it }
}