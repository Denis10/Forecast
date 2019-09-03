package com.vodolazskiy.forecastapplication.core.permissions

import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.UiThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.filter
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicInteger

/**
 * Delegate for [PermissionsManager]
 *
 * @param permissionsObservable - observer for [OnRequestPermissionsResultEvent] events
 * @param activity - Activity.
 * */
class PermissionsManagerDelegate(
    val permissionsObservable: Channel<OnRequestPermissionsResultEvent> = Channel(),
    private val activity: ()-> Activity
) : PermissionsManager {

    /**
     * @see [PermissionsManager.requestPermissions]
     * */
    override suspend fun requestPermissions(permissions: List<String>): RequestPermissionsResult {
        //we always use UI thread to work with permissions
        return withContext(Dispatchers.Main) { requestPermissionsImpl(permissions) }
    }

    /**
     * @see [PermissionsManager.requestPermissionsOrThrow]
     * */
    override suspend fun requestPermissionsOrThrow(permissions: List<String>): RequestPermissionsResult {
        val result = requestPermissions(permissions)
        if (!result.isAllGranted) {
            throw PermissionException(
                "Some permissions were denied: ${result.deniedPermissions.joinToString()}",
                result
            )
        }
        return result
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
    private suspend fun requestPermissionsImpl(permissions: List<String>): RequestPermissionsResult {
        if (!isMarshmallow() || permissions.all { isPermissionGranted(it) }) {
            return RequestPermissionsResult(
                grantedPermissions = permissions,
                deniedPermissions = emptyList()
            )
        }

        // You can request only one permissions set at once;
        // otherwise second permissions set will be denied.
        // So, to prevent it, we use static mutex here
        REQUEST_PERMISSION_MUTEX.lock()
        try {
            val requestCode = generateRequestCode()

            activity().requestPermissions(permissions.toTypedArray(), requestCode)

            //wait for permissions request result
            val event = permissionsObservable.filter { it.requestCode == requestCode }.receive()
            return RequestPermissionsResult(event)
        } finally {
            REQUEST_PERMISSION_MUTEX.unlock()
        }
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

        return activity().packageManager.isPermissionRevokedByPolicy(permission, activity().packageName)
    }

    /**
     * Checks if permissions are available in system
     *
     * @return true if OS is Android 6 or higher
     * */
    private fun isMarshmallow(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M

    /**
     * Generates request code between 1 and 65530
     */
    private fun generateRequestCode(): Int {
        val result = REQUEST_CODE.getAndIncrement()
        if (result >= 65530) {
            REQUEST_CODE.set(1)
        }

        return result
    }

    private companion object {
        private val REQUEST_CODE = AtomicInteger(1)
        private val REQUEST_PERMISSION_MUTEX = Mutex()
    }

}