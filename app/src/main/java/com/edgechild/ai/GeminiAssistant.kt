package com.edgechild.ai

import android.content.Context
import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

object GeminiAssistant {

    private const val TAG = "GeminiAssistant"

    private const val SYSTEM_INSTRUCTION = """
You are a friendly, child-safe AI companion and educational assistant for children aged 6–12 years old named QuestBound Chronicles.

CORE RESPONSIBILITIES:
1. REASON-BASED ACCURACY: Provide a clear, child-friendly, engaging scientific explanation (2-3 short sentences) explaining the exact mechanism or reason.
2. MULTILINGUAL SUPPORT: Automatically detect the language of the user's question, and answer in that EXACT SAME language (e.g. Kannada, English, Hindi, etc.). Provide the correct Android locale tag (e.g., 'kn-IN', 'en-US', 'hi-IN').
3. AVATAR TAGGING: Provide an illustration tag mapping to the topic. Use EXACTLY one of these four tags: [AVATAR: SPACE], [AVATAR: NATURE], [AVATAR: ANIMALS], or [AVATAR: SCIENCE].
4. OUTPUT FORMAT: Respond ONLY with a valid JSON object strictly matching this schema:

{
  "is_safe": true,
  "safety_flag_reason": "NONE",
  "illustration_tag": "<Exact tag, e.g. [AVATAR: SPACE]>",
  "android_locale_tag": "<Locale tag, e.g. en-US or kn-IN>",
  "speech_text": "<Child-friendly answer in the detected language (2-3 short sentences)>",
  "parent_summary_note": "<Short 1-sentence parent note>"
}
"""

    private const val API_KEY = "YOUR_API_KEY"

    private val generativeModel by lazy {
        GenerativeModel(
            modelName = "gemini-3.5-flash",
            apiKey = API_KEY
        )
    }

    suspend fun queryCompanion(context: Context, prompt: String): CompanionResponse {
        return withContext(Dispatchers.IO) {
            val localSafety = SafetyGuardrailEngine.evaluateQuery(prompt)
            if (!localSafety.isSafe) {
                return@withContext CompanionResponse(
                    isSafe = false,
                    safetyFlagReason = localSafety.flagReason,
                    speechText = localSafety.cautionMessage,
                    parentSummaryNote = "Blocked safety query: $prompt",
                    isFallback = false,
                    illustrationTag = "default",
                    localeTag = "en-US"
                )
            }

            try {
                if (API_KEY.isNotBlank()) {
                    val fullPrompt = "$SYSTEM_INSTRUCTION\n\nUser Question: $prompt"
                    val response = generativeModel.generateContent(fullPrompt)
                    val rawText = response.text
                    if (!rawText.isNullOrBlank()) {
                        val parsed = parseCompanionJson(rawText, prompt)
                        if (parsed != null) {
                            return@withContext parsed
                        } else {
                            throw Exception("Failed to parse Gemini response.")
                        }
                    } else {
                        throw Exception("Received empty response from Gemini API.")
                    }
                } else {
                    throw Exception("Invalid or missing Gemini API Key.")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error calling Google Gemini API: ${e.message}", e)
                val msg = e.message ?: ""
                val friendly = when {
                    msg.contains("503", ignoreCase = true) || msg.contains("UNAVAILABLE", ignoreCase = true) || msg.contains("high demand", ignoreCase = true) ->
                        "The AI servers are experiencing high demand. Please try again in a few moments."
                    msg.contains("404", ignoreCase = true) || msg.contains("NOT_FOUND", ignoreCase = true) ->
                        "The selected AI model is currently updating. Please try again later."
                    else ->
                        "Network issue: ${e.localizedMessage}. Please check your connection and retry."
                }
                return@withContext CompanionResponse(
                    isSafe = false,
                    safetyFlagReason = "NETWORK_ERROR",
                    speechText = friendly,
                    parentSummaryNote = "Network error while contacting Gemini.",
                    isFallback = true,
                    illustrationTag = "default",
                    localeTag = "en-US"
                )
            }
        }
    }

    private fun parseCompanionJson(rawJson: String, originalPrompt: String): CompanionResponse? {
        return try {
            val cleanedJson = rawJson.replace("```json", "").replace("```", "").trim()
            val json = JSONObject(cleanedJson)
            val isSafe = json.optBoolean("is_safe", true)
            val speechText = json.optString("speech_text", "")
            val illustrationTag = json.optString("illustration_tag", "[AVATAR: SCIENCE]")
            val localeTag = json.optString("android_locale_tag", "en-US")

            if (speechText.isNotBlank()) {
                CompanionResponse(
                    isSafe = isSafe,
                    safetyFlagReason = json.optString("safety_flag_reason", "NONE"),
                    speechText = speechText,
                    parentSummaryNote = json.optString("parent_summary_note", "Child explored: $originalPrompt"),
                    isFallback = false,
                    illustrationTag = illustrationTag,
                    localeTag = localeTag
                )
            } else null
        } catch (e: Exception) {
            Log.w(TAG, "JSON parse warning for Gemini response", e)
            null
        }
    }
}
