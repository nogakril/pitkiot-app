package com.example.pitkiot.data.models

data class CreateNewGameUiState(
    var gamePin: String? = null,
    override var errorMessage: String? = null
) : UiState