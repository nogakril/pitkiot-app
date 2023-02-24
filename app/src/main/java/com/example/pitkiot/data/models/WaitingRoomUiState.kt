package com.example.pitkiot.data.models

import com.example.pitkiot.data.enums.GameStatus

data class WaitingRoomUiState(
    val gamePin: String? = null,
    val players: List<String> = emptyList(),
    val gameStatus: GameStatus = GameStatus.ADDING_PLAYERS,
    val errorMessage: String? = null
)