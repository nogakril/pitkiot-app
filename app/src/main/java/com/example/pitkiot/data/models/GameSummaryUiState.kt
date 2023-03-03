package com.example.pitkiot.data.models

data class GameSummaryUiState(
    var gamePin: String? = null,
    override var errorMessage: String? = null
) : UiState