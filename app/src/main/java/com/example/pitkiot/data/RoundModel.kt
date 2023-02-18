package com.example.pitkiot.data

// TODO configurable properties
const val SKIPS = 2
const val ROUND_TIME: Long = 60000 // milisecs

data class RoundModel(
    var score: Int = 0,
    var skipsLeft: Int = SKIPS,
    var curWord: String = "",
    var timeLeftToRound: Long = ROUND_TIME
)