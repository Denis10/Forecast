package com.vodolazskiy.forecastapplication.presentation

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.vodolazskiy.forecastapplication.R

class PermissionDialog : DialogFragment() {

    private lateinit var callback: PermissionCallback

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = context as PermissionCallback
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireActivity())
            .setTitle(R.string.location_permission_required)
            .setMessage(R.string.location_permission_explanation)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                callback.onLocationGranted()
            }
            .setNegativeButton(android.R.string.cancel) { _, _ ->
                callback.onLocationDenied()
            }
            .create()

    interface PermissionCallback {

        fun onLocationGranted()

        fun onLocationDenied()
    }

    companion object {

        fun newInstance() = PermissionDialog()
    }
}
