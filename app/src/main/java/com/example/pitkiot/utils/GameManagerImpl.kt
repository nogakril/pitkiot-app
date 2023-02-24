package com.example.pitkiot.utils

import com.example.pitkiot.data.enums.Team
import com.example.pitkiot.utils.GameManagerImpl.TeamState.Companion.updateNextPlayer


class GameManagerImpl(playersTeamA: List<String>, playersTeamB: List<String>): GameManager {

    private var mapTeamToState: Map<Team, TeamState>
    private var curTeam = Team.TEAM_A

    init {
        mapTeamToState = mapOf(Team.TEAM_A to TeamState(team = Team.TEAM_A), Team.TEAM_B to TeamState(team = Team.TEAM_B))
    }

    override fun setPlayers(team: Team, players: List<String>) {
        mapTeamToState[team]!!.teamPlayers = players
    }

    override fun getNextTeam(): Team = if (curTeam == Team.TEAM_B) Team.TEAM_A else Team.TEAM_B

    override fun getNextPlayer(): String {
        mapTeamToState[curTeam]!!.updateNextPlayer()
        return mapTeamToState[curTeam]!!.curPlayer!!
    }

    override fun getTeamScore(team: Team) = mapTeamToState[team]!!.score

    override fun setTeamScore(team: Team, score: Int) {
        mapTeamToState[team]!!.score = score
    }

    data class TeamState(
        var curPlayer: String? = null,
        var team: Team,
        var score: Int = 0,
        var teamPlayers: List<String>?= emptyList()
    ) {
        companion object{
            fun TeamState.updateNextPlayer() {
                val currentPlayerIndex = teamPlayers!!.indexOf(curPlayer ?: 0)
                val nextPlayerIndex = (currentPlayerIndex + 1) % teamPlayers!!.size
                curPlayer = teamPlayers!![nextPlayerIndex]
            }
        }
    }

}