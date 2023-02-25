package com.example.pitkiot.data.enums

enum class GameStatus(status: String) {
    ADDING_PLAYERS("adding_players"),
    ADDING_WORDS("adding_words"),
    IN_GAME("in_game");

    companion object {
        fun fromString(status: String): GameStatus = when (status) {
            "adding_players" -> ADDING_PLAYERS
            "adding_words" -> ADDING_WORDS
            else -> { IN_GAME }
        }
    }
}