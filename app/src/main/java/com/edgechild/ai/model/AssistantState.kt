package com.edgechild.ai.model

import com.edgechild.ai.IllustrationInfo

data class AssistantState(
    val query: String = "",
    val answer: String = "",
    val illustration: IllustrationInfo? = IllustrationInfo("Curiosity & Science", "🧪", com.edgechild.ai.R.drawable.ic_illustration_default),
    val isListening: Boolean = false,
    val isLoading: Boolean = false
)
