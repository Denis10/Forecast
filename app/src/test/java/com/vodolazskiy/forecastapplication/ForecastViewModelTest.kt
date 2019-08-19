package com.vodolazskiy.forecastapplication

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.vodolazskiy.forecastapplication.domain.ForecastUsecase
import com.vodolazskiy.forecastapplication.domain.entity.Forecast
import com.vodolazskiy.forecastapplication.domain.entity.ForecastItem
import com.vodolazskiy.forecastapplication.presentation.ForecastViewModel
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

@ExperimentalCoroutinesApi
class ForecastViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutinesTestRule = CoroutinesTestRule()
    @get:Rule
    var commonTestRule = CommonTestRule()

    lateinit var viewModel: ForecastViewModel

    @Before
    fun setUp() {
        viewModel = ForecastViewModel(forecastUsecase)
        viewModel.setTestContext(coroutinesTestRule.viewModelWithErrorContext)

        every { loadingObserver.onChanged(any()) } just Runs
    }

    @Test
    fun `updateForecast success`() = coroutinesTestRule.mainDispatcher.runBlockingTest {
        // arrange
        val forecastItems = listOf(forecastItem)
        every { forecast.forecasts } returns forecastItems
        coEvery { forecastUsecase.getForecast() } returns forecast
        viewModel.isLoading.observeForever(loadingObserver)
        // act
        viewModel.updateForecast()
        // assert
        verify { loadingObserver.onChanged(true) }
        assertEquals(forecastItems, LiveDataTestUtil.getValue(viewModel.forecasts))
        assertEquals(false, LiveDataTestUtil.getValue(viewModel.isLoading))
    }

    @Test
    fun `updateForecast failure`() = coroutinesTestRule.mainDispatcher.runBlockingTest {
        // arrange
        val exception = Exception()
        coEvery { forecastUsecase.getForecast() } throws exception
        viewModel.isLoading.observeForever(loadingObserver)
        // act
        viewModel.updateForecast()
        // assert
        verify { loadingObserver.onChanged(true) }
        assertEquals(null, viewModel.error.value)
        assertEquals(null, LiveDataTestUtil.getValue(viewModel.forecasts))
        assertEquals(false, LiveDataTestUtil.getValue(viewModel.isLoading))
        assertEquals(exception, LiveDataTestUtil.getValue(viewModel.error))
    }

    @Test
    fun `getForecast loading blocked`() = coroutinesTestRule.mainDispatcher.runBlockingTest {
        // arrange
        viewModel.isLoadingInternal.value = true
        // act
        viewModel.updateForecast()
        // assert
        coVerify(exactly = 0) { forecastUsecase.getForecast() }
        assertEquals(null, LiveDataTestUtil.getValue(viewModel.forecasts))
    }

    @Test
    fun `getForecast success`() = coroutinesTestRule.mainDispatcher.runBlockingTest {
        // arrange
        val forecastItems = listOf(forecastItem)
        every { forecast.forecasts } returns forecastItems
        coEvery { forecastUsecase.getForecast() } returns forecast
        viewModel.isLoading.observeForever(loadingObserver)
        // act
        viewModel.updateForecast()
        // assert
        verify { loadingObserver.onChanged(true) }
        assertEquals(forecastItems, LiveDataTestUtil.getValue(viewModel.forecasts))
        assertEquals(false, LiveDataTestUtil.getValue(viewModel.isLoading))
    }

    companion object {
        @JvmStatic
        private lateinit var forecastUsecase: ForecastUsecase
        @JvmStatic
        private lateinit var loadingObserver: Observer<Boolean>
        @JvmStatic
        private lateinit var forecast: Forecast
        @JvmStatic
        private lateinit var forecastItem: ForecastItem

        @BeforeClass
        @JvmStatic
        fun initialize() {
            forecastUsecase = mockk()
            loadingObserver = mockk()
            forecast = mockk()
            forecastItem = mockk()
        }
    }
}
