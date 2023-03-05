package com.example.pitkiot.data.models

import com.example.pitkiot.data.enums.Team
import com.example.pitkiot.viewmodel.RoundViewModel.Companion.ROUND_TIME
import com.example.pitkiot.viewmodel.RoundViewModel.Companion.SKIPS

data class RoundUiState(
    var score: Int = 0,
    var skipsLeft: Int = SKIPS,
    var curWord: String = "",
    var timeLeftToRound: Long = ROUND_TIME,
    var curTeam: Team = Team.TEAM_A,
    var curPlayer: String = "",
    var gameEnded: Boolean = false,
    var showTeamsDivisionDialog: Boolean = false,
    var inRound: Boolean = false,
    var allPitkiot: Set<String> = emptySet(),
    var allPlayers: List<String> = emptyList(),
    var usedWords: MutableSet<String> = mutableSetOf(),
    var skippedWords: MutableSet<String> = mutableSetOf(),
    var teamAScore: Int = 0,
    var playerIndexTeamA: Int = 0,
    var playersTeamA: List<String> = emptyList(),
    var teamBScore: Int = 0,
    var playerIndexTeamB: Int = 0,
    var playersTeamB: List<String> = emptyList(),
    override var errorMessage: String? = null
) : UiState