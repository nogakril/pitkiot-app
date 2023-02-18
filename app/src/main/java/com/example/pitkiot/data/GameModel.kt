package com.example.pitkiot.data

const val ADD_WORDS_TIME: Long = 120000

data class GameModel(
    var gameStarted: Boolean = false,
    var timeLeftToAddWords: Long = ROUND_TIME
)
