package com.vodolazskiy.forecastapplication.core.permissions

import androidx.annotation.UiThread

/**
 * Interface to work with permissions
 * */
interface PermissionsManager {
    /**
     * Asks about specified permissions
     *
     * @param permissions - list of permissions to request
     * @return [RequestPermissionsResult]
     * */
    @UiThread
    fun requestPermissions(requestCode: Int, vararg permissions: String) =
        requestPermissions(requestCode, permissions.toList())

    /**
     * Asks about specified permissions
     *
     * @param permissions - list of permissions to request
     * @return [RequestPermissionsResult]
     * */
    @UiThread
    fun requestPermissions(requestCode: Int, permissions: List<String>)

    @UiThread
    suspend fun listenForCode(requestCode: Int): RequestPermissionsResult

    /**
     * Checks if [permission] needs explanation dialog
     *
     * @param permission - permission to check
     * @return true if [permission] needs explanation dialog
     * */
    @UiThread
    fun shouldShowRequestPermissionRationale(permission: String): Boolean

    /**
     * Checks if [permission] granted
     *
     * @param permission - permission to check
     * @return true if [permission] is granted
     * */
    @UiThread
    fun isPermissionGranted(permission: String): Boolean
}