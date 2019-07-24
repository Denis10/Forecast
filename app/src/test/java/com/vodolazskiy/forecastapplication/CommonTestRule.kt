package com.vodolazskiy.forecastapplication

import io.mockk.clearAllMocks
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class CommonTestRule : TestWatcher() {

    override fun finished(description: Description?) {
        super.finished(description)
        clearAllMocks()
    }
}