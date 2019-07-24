package com.vodolazskiy.forecastapplication

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.util.*
import kotlin.coroutines.CoroutineContext

@ExperimentalCoroutinesApi
class CoroutinesTestRule(
        val mainDispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher(),
        val viewModelDispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()
) : TestWatcher() {

    private val exceptions = Collections.synchronizedList(mutableListOf<Throwable>())

    private val errorDispatcher: CoroutineContext
        get() = CoroutineExceptionHandler { _, throwable ->
            exceptions.add(throwable)
        }

    val viewModelWithErrorContext: CoroutineContext = viewModelDispatcher + errorDispatcher

    override fun starting(description: Description?) {
        super.starting(description)
        Dispatchers.setMain(mainDispatcher)

        exceptions.clear()
    }

    override fun finished(description: Description?) {
        super.finished(description)
        Dispatchers.resetMain()
        mainDispatcher.cleanupTestCoroutines()
        viewModelDispatcher.cleanupTestCoroutines()

        exceptions.forEach { throw AssertionError(it) }
    }
}