package com.example.pitkiot.data.models

import com.example.pitkiot.data.enums.GameStatus
import com.example.pitkiot.data.enums.GameStatus.IN_GAME
import com.example.pitkiot.data.enums.Team
import com.example.pitkiot.viewmodel.ROUND_TIME
import com.example.pitkiot.viewmodel.SKIPS

data class RoundUiState(
    var score: Int = 0,
    var skipsLeft: Int = SKIPS,
    var curWord: String = "",
    var timeLeftToRound: Long = ROUND_TIME,
    var curTeam: Team,
    var curPlayer: String,
    var gameEnded: Boolean = false,
    var teamAScore: Int = 0,
    var teamBScore: Int = 0,
    var showTeamsDivisionDialog: Boolean = false,
    var inRound: Boolean = false,
    var gameStatus: GameStatus = IN_GAME,
    override var errorMessage: String? = null
) : UiState