package com.example.pitkiot.utils

import com.example.pitkiot.data.enums.Team

interface GameManager {

    fun setPlayers(team: Team, players: List<String>)

    fun getNextTeam(): Team

    fun getNextPlayer(): String

    fun getTeamScore(team: Team): Int

    fun setTeamScore(team: Team, score: Int)
}