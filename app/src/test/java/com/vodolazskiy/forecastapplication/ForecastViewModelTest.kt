package com.vodolazskiy.forecastapplication

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.vodolazskiy.forecastapplication.domain.ForecastUsecase
import com.vodolazskiy.forecastapplication.domain.entity.Forecast
import com.vodolazskiy.forecastapplication.domain.entity.ForecastItem
import com.vodolazskiy.forecastapplication.presentation.ForecastViewModel
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

class ForecastViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @MockK
    lateinit var forecastUsecase: ForecastUsecase

    @ExperimentalCoroutinesApi
    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        Dispatchers.setMain(Dispatchers.Default)
    }

    @ExperimentalCoroutinesApi
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `updateForecast success`() {
        // arrange
        val viewModel = ForecastViewModel(forecastUsecase)
        val forecastItems = mockk<List<ForecastItem>>()
        val forecast = mockk<Forecast> {
            every { forecasts } returns forecastItems
        }
        coEvery { forecastUsecase.getForecast() } returns forecast
        // act
        viewModel.updateForecast()
        // assert
        assertEquals(true, viewModel.isLoading.value)
        assertEquals(forecastItems, LiveDataTestUtil.getValue(viewModel.forecasts))
        assertEquals(false, LiveDataTestUtil.getValue(viewModel.isLoading))
    }

    @Test
    fun `updateForecast failure`() {
        // arrange
        val viewModel = ForecastViewModel(forecastUsecase)
        val exception = Exception()
        coEvery { forecastUsecase.getForecast() } throws exception
        // act
        viewModel.updateForecast()
        // assert
        assertEquals(true, viewModel.isLoading.value)
        assertEquals(null, viewModel.error.value)
        assertEquals(null, LiveDataTestUtil.getValue(viewModel.forecasts))
        assertEquals(false, LiveDataTestUtil.getValue(viewModel.isLoading))
        assertEquals(exception, LiveDataTestUtil.getValue(viewModel.error))
    }

    @Test
    fun `getForecast loading blocked`() {
        // arrange
        val viewModel = ForecastViewModel(forecastUsecase)
        // act
        viewModel.isLoadingInternal.value = true
        viewModel.updateForecast()
        // assert
        coVerify(exactly = 0) { forecastUsecase.getForecast() }
        assertEquals(null, LiveDataTestUtil.getValue(viewModel.forecasts))
    }

    @Test
    fun `getForecast success`() {
        // arrange
        val viewModel = ForecastViewModel(forecastUsecase)
        val forecastItems = mockk<List<ForecastItem>>()
        val forecast = mockk<Forecast> {
            every { forecasts } returns forecastItems
        }
        coEvery { forecastUsecase.getForecast() } returns forecast
        // act
        viewModel.updateForecast()
        // assert
        assertEquals(true, viewModel.isLoading.value)
        assertEquals(forecastItems, LiveDataTestUtil.getValue(viewModel.forecasts))
        assertEquals(false, LiveDataTestUtil.getValue(viewModel.isLoading))
    }
}
