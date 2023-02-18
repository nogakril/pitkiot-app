package com.example.pitkiot.data

const val ADD_WORDS_TIME: Long = 120000

data class GameModel(
    var gameStatus: GameStatus = GameStatus.GAME_CREATION,
    var timeLeftToAddWords: Long = ROUND_TIME
)
