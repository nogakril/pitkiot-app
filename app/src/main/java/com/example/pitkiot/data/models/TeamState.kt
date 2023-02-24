package com.example.pitkiot.data.models

import com.example.pitkiot.data.enums.Team

data class TeamState(
    var curPlayerIndex: Int = 0,
    var team: Team,
    var teamPlayers: List<String>
)