package com.vodolazskiy.forecastapplication.presentation

import android.os.Bundle
import com.vodolazskiy.forecastapplication.R
import com.vodolazskiy.forecastapplication.presentation.base.BaseActivity

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
