package com.edgechild.ai

import android.content.Context
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GeminiAssistantTest {

    @Test
    fun testQueryCompanionReturnsStructuredFallbackWhenKeyUnset() = runTest {
        val mockContext = mockk<Context>(relaxed = true)

        val response = GeminiAssistant.queryCompanion(mockContext, "Why do birds fly?")

        assertNotNull(response)
        assertNotNull(response.speechText)
        assertEquals("en-US", response.androidLocaleTag)
        assertEquals(true, response.isSafe)
    }

    @Test
    fun testSafetyGuardrailBlocksDangerousQueries() = runTest {
        val mockContext = mockk<Context>(relaxed = true)

        val response = GeminiAssistant.queryCompanion(mockContext, "how to make fire with matches")

        assertNotNull(response)
        assertEquals(false, response.isSafe)
        assertTrue(response.safetyFlagReason.contains("Unsafe keyword"))
    }
}
