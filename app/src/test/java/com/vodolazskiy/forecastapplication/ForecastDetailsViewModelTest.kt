package com.vodolazskiy.forecastapplication

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.vodolazskiy.forecastapplication.domain.entity.ForecastItem
import com.vodolazskiy.forecastapplication.presentation.forecastdetails.ForecastDetailsViewModel
import io.mockk.*
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class ForecastDetailsViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()
    @get:Rule
    var commonTestRule = CommonTestRule()

    lateinit var viewModel: ForecastDetailsViewModel

    @Before
    fun setUp() {
        viewModel = ForecastDetailsViewModel()
    }

    @Test
    fun `set data`() {
        //arrange
        viewModel.forecast.observeForever(forecastObserver)
        every { forecastObserver.onChanged(any()) } just Runs
        //act
        viewModel.setData(forecastItem)
        //assert
        verify { forecastObserver.onChanged(forecastItem) }
    }

    companion object {
        @JvmStatic
        private lateinit var forecastItem: ForecastItem
        @JvmStatic
        private lateinit var forecastObserver: Observer<ForecastItem>

        @BeforeClass
        @JvmStatic
        fun initialize() {
            forecastItem = mockk()
            forecastObserver = mockk()
        }
    }
}