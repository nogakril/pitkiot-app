package com.example.myapplication.data

// TODO configurable properties
const val SKIPS = 2
const val COUNTDOWN: Long = 60000 // milisecs

data class RoundModel(
    var score: Int = 0,
    var skipsLeft: Int = SKIPS,
    var curWord: String = "",
    var countDown: Long = COUNTDOWN
)