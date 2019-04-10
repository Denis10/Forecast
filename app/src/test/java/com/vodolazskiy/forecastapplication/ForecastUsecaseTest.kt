package com.vodolazskiy.forecastapplication

import com.vodolazskiy.forecastapplication.domain.CurrentLocationService
import com.vodolazskiy.forecastapplication.domain.ForecastService
import com.vodolazskiy.forecastapplication.domain.ForecastUsecase
import com.vodolazskiy.forecastapplication.domain.ForecastUsecaseImpl
import com.vodolazskiy.forecastapplication.domain.entity.Forecast
import com.vodolazskiy.forecastapplication.domain.exceptions.ServiceError
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ForecastUsecaseTest {

    @MockK
    lateinit var locationService: CurrentLocationService
    @MockK
    lateinit var forecastService: ForecastService
    lateinit var forecastUsecase: ForecastUsecase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        forecastUsecase = ForecastUsecaseImpl(locationService, forecastService)
    }

    @Test
    fun `getForecast success`() {
        // arrange
        val forecast = mockk<Forecast>()
        coEvery { locationService.getCurrentLocation() } returns Pair(45.0, 45.0)
        coEvery { forecastService.fiveDaysForecast(any(), any()) } returns forecast

        runBlocking {
            // act
            val res = forecastUsecase.getForecast()
            // assert
            coVerify { forecastService.fiveDaysForecast(45.0, 45.0) }
            assertEquals(forecast, res)
        }
    }

    @Test
    fun `getForecast location failure`() {
        // arrange
        val forecast = mockk<Forecast>()
        val exception = ServiceError.NoLocationException()
        coEvery { locationService.getCurrentLocation() } throws exception
        coEvery { forecastService.fiveDaysForecast(any(), any()) } returns forecast

        runBlocking {
            // act
            var ex: Exception? = null
            try {
                forecastUsecase.getForecast()
            } catch (e: Exception) {
                ex = e
            }

            // assert
            coVerify(exactly = 0) { forecastService.fiveDaysForecast(any(), any()) }
            assertEquals(exception, ex)
        }
    }

    @Test
    fun `getForecast forecast failure`() {
        // arrange
        val exception = ServiceError.ApiError()
        coEvery { locationService.getCurrentLocation() } returns Pair(45.0, 45.0)
        coEvery { forecastService.fiveDaysForecast(any(), any()) } throws exception

        runBlocking {
            // act
            var ex: Exception? = null
            try {
                forecastUsecase.getForecast()
            } catch (e: Exception) {
                ex = e
            }
            // assert
            coVerify { locationService.getCurrentLocation() }
            assertEquals(exception, ex)
        }
    }
}
