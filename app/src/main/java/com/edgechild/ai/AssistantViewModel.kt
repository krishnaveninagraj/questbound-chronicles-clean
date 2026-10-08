package com.edgechild.ai

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edgechild.ai.model.AssistantState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AssistantViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private var voicePipelineManager: VoicePipelineManager = VoicePipelineManager(
        context = context,
        onPartialResult = { text ->
            if (text.isNotBlank()) {
                _state.update { it.copy(query = text) }
            }
        },
        onFinalResult = { text ->
            if (text.isNotBlank()) {
                _state.update { it.copy(query = text, isListening = false) }
                submitQuery(text)
            } else {
                _state.update { it.copy(isListening = false) }
            }
        },
        onError = { _ ->
            _state.update { it.copy(isListening = false) }
        }
    )

    var ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    private val _state = MutableStateFlow(AssistantState())
    val state: StateFlow<AssistantState> = _state.asStateFlow()

    fun startListening() {
        _state.update { it.copy(isListening = true, query = "", answer = "") }
        voicePipelineManager.startListening()
    }

    fun submitQuery(rawQuery: String) {
        if (rawQuery.isBlank()) return

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, query = rawQuery) }
            try {
                val companionResponse = withContext(ioDispatcher) {
                    GeminiAssistant.queryCompanion(context, rawQuery)
                }
                val illustration = IllustrationMapper.getIllustration(
                    rawQuery,
                    companionResponse.speechText,
                    companionResponse.illustrationTag
                )

                voicePipelineManager.speak(companionResponse.speechText, companionResponse.localeTag)

                _state.update {
                    it.copy(
                        query = rawQuery,
                        answer = companionResponse.speechText,
                        illustration = illustration,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _state.update { 
                    it.copy(
                        isLoading = false,
                        answer = e.message ?: "Oops! An unknown error occurred."
                    )
                }
            }
        }
    }

    fun stopSpeaking() {
        voicePipelineManager.stopSpeaking()
    }

    override fun onCleared() {
        super.onCleared()
        voicePipelineManager.destroy()
    }
}
