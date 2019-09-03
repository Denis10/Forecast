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
    suspend fun requestPermissions(vararg permissions: String) = requestPermissions(permissions.toList())

    /**
     * Asks about specified permissions
     *
     * @param permissions - list of permissions to request
     * @return [RequestPermissionsResult]
     * */
    @UiThread
    suspend fun requestPermissions(permissions: List<String>): RequestPermissionsResult

    /**
     * Asks about specified permissions. Throws exception if not all permissions were granted
     *
     * @param permissions - list of permissions to request
     * @return [RequestPermissionsResult]
     * */
    @UiThread
    suspend fun requestPermissionsOrThrow(vararg permissions: String) = requestPermissionsOrThrow(permissions.toList())

    /**
     * Asks about specified permissions. Throws exception if not all permissions were granted
     *
     * @param permissions - list of permissions to request
     * @return [RequestPermissionsResult]
     * */
    @UiThread
    suspend fun requestPermissionsOrThrow(permissions: List<String>): RequestPermissionsResult

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