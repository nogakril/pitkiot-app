package com.example.pitkiot.data.models

data class JoinGameUiState(
    var gamePin: String? = null,
    override var errorMessage: String? = null
) : UiState