package com.example.pitkiot.data.models

import com.example.pitkiot.data.enums.GameStatus
import com.example.pitkiot.data.enums.GameStatus.ADDING_PLAYERS

data class WaitingRoomUiState(
    var gamePin: String? = null,
    var players: List<String> = emptyList(),
    var gameStatus: GameStatus = ADDING_PLAYERS,
    override var errorMessage: String? = null
) : UiState