package com.example.pitkiot.data.models

import com.example.pitkiot.data.enums.GameStatus

data class AddWordsUiState(
    val gamePin: String? = null,
    val gameStatus: GameStatus = GameStatus.ADDING_WORDS,
    val errorMessage: String? = null
)