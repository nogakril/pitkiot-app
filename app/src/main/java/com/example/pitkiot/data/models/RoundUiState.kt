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
    var curTeam: Team = Team.TEAM_A,
    var curPlayer: String = "",
    var gameEnded: Boolean = false,
    var showTeamsDivisionDialog: Boolean = false,
    var showStartBtn: Boolean = false,
    var inRound: Boolean = false,
    override var errorMessage: String? = null,
    var allPitkiot: Set<String> = emptySet(),
    var allPlayers: List<String> = emptyList(),
    var usedWords: MutableSet<String> = mutableSetOf(),
    var skippedWords: MutableSet<String> = mutableSetOf(),

    var teamAScore: Int = 0, // remove?
    var playerIndexTeamA: Int = 0,
    var playersTeamA: List<String> = emptyList(),

    var teamBScore: Int = 0, //remove?
    var playerIndexTeamB: Int = 0,
    var playersTeamB: List<String> = emptyList()
) : UiState