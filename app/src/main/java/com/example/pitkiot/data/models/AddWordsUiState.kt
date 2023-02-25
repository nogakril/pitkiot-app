package com.example.pitkiot.data.models

import com.example.pitkiot.data.enums.GameStatus
import com.example.pitkiot.data.enums.GameStatus.ADDING_WORDS

data class AddWordsUiState(
    var gamePin: String? = null,
    var gameStatus: GameStatus = ADDING_WORDS,
    override var errorMessage: String? = null
) : UiState