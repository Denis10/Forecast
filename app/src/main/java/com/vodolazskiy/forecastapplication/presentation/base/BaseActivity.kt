package com.vodolazskiy.forecastapplication.presentation.base

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vodolazskiy.forecastapplication.core.permissions.OnRequestPermissionsResultEvent
import com.vodolazskiy.forecastapplication.core.permissions.PermissionsManagerDelegate
import dagger.android.AndroidInjection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

private const val PERMISSION_FLOW_IN_PROGRESS = "PERMISSION_FLOW_IN_PROGRESS"

abstract class BaseActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    protected var permissionFlowInProgress = false

    protected val permissionsManager = PermissionsManagerDelegate(activity = { this })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AndroidInjection.inject(this)

        permissionFlowInProgress =
            savedInstanceState?.getBoolean(PERMISSION_FLOW_IN_PROGRESS) ?: false
    }


    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(PERMISSION_FLOW_IN_PROGRESS, permissionFlowInProgress)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
        permissionsManager.permissionsObservable.close()
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
}
