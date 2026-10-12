package com.example.rewriteit

import com.example.rewriteit.data.RewriterRepository
import com.example.rewriteit.model.ModelStatus
import com.example.rewriteit.model.RewriteStyle
import com.example.rewriteit.ui.viewmodel.RewriteViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RewriteViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private class FakeRewriterRepository : RewriterRepository {
        var shouldSucceed = true
        var mockResult = "Fake rewritten text"

        override fun checkModelStatus(): Flow<ModelStatus> = flowOf(ModelStatus.READY)

        override suspend fun downloadModel(): Boolean = true

        override suspend fun rewrite(text: String, style: RewriteStyle): Result<String> {
            return if (shouldSucceed) {
                Result.success("$mockResult for style ${style.displayName}")
            } else {
                Result.failure(RuntimeException("Error rewriting text"))
            }
        }
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `test initial state and model status check`() = runTest {
        val repository = FakeRewriterRepository()
        val viewModel = RewriteViewModel(repository)

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("", state.inputText)
        assertEquals(RewriteStyle.REPHRASE, state.selectedStyle)
        assertEquals(ModelStatus.READY, state.modelStatus)
        assertEquals("", state.rewrittenResult)
        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
    }

    @Test
    fun `test input text change and style selection`() = runTest {
        val repository = FakeRewriterRepository()
        val viewModel = RewriteViewModel(repository)

        viewModel.onInputTextChanged("Hello world")
        viewModel.onStyleSelected(RewriteStyle.EMOJIFY)

        val state = viewModel.uiState.value
        assertEquals("Hello world", state.inputText)
        assertEquals(RewriteStyle.EMOJIFY, state.selectedStyle)
    }

    @Test
    fun `test rewrite text success`() = runTest {
        val repository = FakeRewriterRepository()
        val viewModel = RewriteViewModel(repository)

        viewModel.onInputTextChanged("Hello world")
        viewModel.onStyleSelected(RewriteStyle.PROFESSIONAL)
        viewModel.rewriteText()

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals("Fake rewritten text for style Professional", state.rewrittenResult)
        assertEquals(1, state.history.size)
        assertEquals("Hello world", state.history.first().originalText)
        assertEquals(RewriteStyle.PROFESSIONAL, state.history.first().style)
    }

    @Test
    fun `test rewrite empty text sets error`() = runTest {
        val repository = FakeRewriterRepository()
        val viewModel = RewriteViewModel(repository)

        viewModel.onInputTextChanged("   ")
        viewModel.rewriteText()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals("Please enter text to rewrite", state.errorMessage)
    }

    @Test
    fun `test swap result to input`() = runTest {
        val repository = FakeRewriterRepository()
        val viewModel = RewriteViewModel(repository)

        viewModel.onInputTextChanged("Original text")
        viewModel.rewriteText()
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.swapResultToInput()

        val state = viewModel.uiState.value
        assertEquals("Fake rewritten text for style Rephrase", state.inputText)
        assertEquals("", state.rewrittenResult)
    }
}
