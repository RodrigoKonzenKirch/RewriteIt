package com.example.rewriteit.model

import java.util.UUID

data class RewriteHistoryItem(
    val id: String = UUID.randomUUID().toString(),
    val originalText: String,
    val style: RewriteStyle,
    val rewrittenText: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class RewriteUiState(
    val inputText: String = "",
    val selectedStyle: RewriteStyle = RewriteStyle.REPHRASE,
    val modelStatus: ModelStatus = ModelStatus.UNCHECKED,
    val downloadProgress: Float = 0f,
    val isLoading: Boolean = false,
    val rewrittenResult: String = "",
    val errorMessage: String? = null,
    val history: List<RewriteHistoryItem> = emptyList()
)
