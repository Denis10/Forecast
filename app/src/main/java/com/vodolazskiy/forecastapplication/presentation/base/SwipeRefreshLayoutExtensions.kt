package com.vodolazskiy.forecastapplication.presentation.base

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

fun SwipeRefreshLayout.setRefreshLock(canRefresh: () -> Boolean) {
    setOnChildScrollUpCallback { _, target ->
        when {
            !canRefresh.invoke() -> true
            else -> target?.canScrollVertically(-1) ?: false
        }
    }
}
