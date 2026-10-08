package com.edgechild.ai

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AssistantViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `submitQuery processes prompt and updates state`() = runTest(testDispatcher) {
        val mockContext = mockk<Context>(relaxed = true)
        val mockVoiceManager = mockk<VoicePipelineManager>(relaxed = true)

        justRun { mockVoiceManager.speak(any(), any()) }
        justRun { mockVoiceManager.stopSpeaking() }

        val viewModel = AssistantViewModel(mockContext, mockVoiceManager, testDispatcher)

        viewModel.submitQuery("Why do birds fly?")
        testDispatcher.scheduler.advanceUntilIdle()
        Thread.sleep(500)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals("Why do birds fly?", state.query)
        assertNotNull(state.answer)
        assertEquals(false, state.isLoading)

        verify { mockVoiceManager.speak(any(), any()) }
    }
}
