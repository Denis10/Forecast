package com.vodolazskiy.forecastapplication.core.permissions

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.UiThread
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.filter

/**
 * Delegate for [PermissionsManager]
 *
 * @param permissionsObservable - observer for [OnRequestPermissionsResultEvent] events
 * @param activity - Activity.
 * */
class PermissionsManagerDelegate(
    val permissionsObservable: Channel<OnRequestPermissionsResultEvent> = Channel(),
    private val activity: () -> Activity
) : PermissionsManager {

    /**
     * @see [PermissionsManager.requestPermissions]
     * */
    override fun requestPermissions(requestCode: Int, permissions: List<String>) {
        //we always use UI thread to work with permissions
        return requestPermissionsImpl(requestCode, permissions)
    }

    /**
     * Implementation of permission rational status request. Should be called on UI thread
     *
     * @param permission - permission to check
     * @return true if permission needs explanation
     * */
    @UiThread
    override fun shouldShowRequestPermissionRationale(permission: String): Boolean {
        if (!isMarshmallow()) return false

        return activity().shouldShowRequestPermissionRationale(permission)
    }

    /**
     * Implementation of permissions request. Should be called on UI thread
     *
     * @param permissions - list of permissions to request
     * @return [RequestPermissionsResult]
     * */
    @UiThread
    private fun requestPermissionsImpl(requestCode: Int, permissions: List<String>) {
        if (!isMarshmallow() || permissions.all { isPermissionGranted(it) }) return

        activity().requestPermissions(permissions.toTypedArray(), requestCode)
    }

    override suspend fun listenForCode(requestCode: Int): RequestPermissionsResult {
        val event = permissionsObservable.filter { it.requestCode == requestCode }.receive()
        return RequestPermissionsResult(event)
    }

    /**
     * Implementation of permission granted status request. Should be called on UI thread
     *
     * @param permission - permission to check
     * @return true if permission is granted
     * */
    @UiThread
    override fun isPermissionGranted(permission: String): Boolean {
        if (!isMarshmallow()) return true

        return activity().checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Check if permission is revoked
     *
     * @param permission - permission to check
     * @return true if permission is revoked
     * */
    private fun isPermissionRevoked(permission: String): Boolean {
        if (!isMarshmallow()) return false

        return activity().packageManager.isPermissionRevokedByPolicy(
            permission,
            activity().packageName
        )
    }

    /**
     * Checks if permissions are available in system
     *
     * @return true if OS is Android 6 or higher
     * */
    private fun isMarshmallow(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
}