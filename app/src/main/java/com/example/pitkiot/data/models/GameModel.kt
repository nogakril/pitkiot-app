package com.example.pitkiot.data.models

import com.example.pitkiot.data.enums.GameStatus
import com.example.pitkiot.data.enums.Role
import com.example.pitkiot.data.enums.Team
import com.example.pitkiot.viewmodel.ROUND_TIME

const val ADD_WORDS_TIME: Long = 120000

data class GameModel(
    var gameId: String?,
    var userRole: Role = Role.ADMIN,
    var team: Team = Team.TEAM_A,
    var gameStatus: GameStatus = GameStatus.GAME_CREATION,
    var players: List<String> = emptyList(),
    var words: List<String> = emptyList(),
    var timeLeftToAddWords: Long = ROUND_TIME
)