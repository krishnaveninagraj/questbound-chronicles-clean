package com.edgechild.ai

data class CompanionResponse(
    val isSafe: Boolean,
    val safetyFlagReason: String,
    val speechText: String,
    val parentSummaryNote: String,
    val isFallback: Boolean,
    val illustrationTag: String,
    val localeTag: String
)
