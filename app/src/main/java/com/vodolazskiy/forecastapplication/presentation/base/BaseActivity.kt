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



abstract class BaseActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    protected val permissionsManager = PermissionsManagerDelegate(activity = { this })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AndroidInjection.inject(this)
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
