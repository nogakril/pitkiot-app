package com.example.pitkiot.viewmodel

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pitkiot.data.PitkiotRepository
import com.example.pitkiot.data.enums.Team
/* ktlint-disable */
import com.example.pitkiot.data.enums.Team.*
import com.example.pitkiot.data.models.RoundUiState
import com.example.pitkiot.data.models.TeamState
/* ktlint-enable */
import kotlinx.coroutines.launch

const val SKIPS = 2
const val ROUND_TIME: Long = 10000 // milisecs

class RoundViewModel(
    private val gamePin: String,
    private val pitkiotRepository: PitkiotRepository,
) : ViewModel() {

    private lateinit var allPitkiot: Set<String>
    private lateinit var allPlayers: List<String>
    private var usedWords: MutableSet<String> = mutableSetOf()
    private var skippedWords: MutableSet<String> = mutableSetOf()
    private lateinit var teamInfo: Map<Team, TeamState>

    private val _uiState = MutableLiveData<RoundUiState>()
    val uiState: LiveData<RoundUiState> = _uiState

    private val roundTimer = object : CountDownTimer(ROUND_TIME, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            _uiState.postValue(_uiState.value!!.copy(timeLeftToRound = millisUntilFinished / 1000))
        }
        override fun onFinish() {
            resetSkippedWords()
            if (usedWords.size != allPitkiot.size) {
                usedWords.remove(_uiState.value!!.curWord)
                setTeamScore(_uiState.value!!.curTeam, _uiState.value!!.score)
                val nextTeam = getNextTeam()
                _uiState.postValue(_uiState.value!!.copy(curTeam = nextTeam, curPlayer = teamInfo[nextTeam]!!.getNextPlayer()))
            } else {
                endGame()
            }
        }
    }

    init {
        getAndSetPlayers()
        initGame()
    }

    private fun initGame() {
        viewModelScope.launch {
            _uiState.postValue(RoundUiState(curTeam = TEAM_A, curPlayer = teamInfo[TEAM_A]!!.getNextPlayer()))
            pitkiotRepository.getWords(gamePin).onSuccess { result ->
                allPitkiot = result.words.toSet()
            }.onFailure {
                _uiState.postValue(_uiState.value!!.copy(errorMessage = "Error getting all pitkiot of game $gamePin"))
            }
        }
    }

    private fun setTeamScore(team: Team, score: Int) {
        if (team == TEAM_A) _uiState.value!!.teamAScore += score else _uiState.value!!.teamBScore += score
    }

    fun onGameEndedReturnWinner(): Team {
        if (_uiState.value!!.teamAScore == _uiState.value!!.teamBScore) {
            return NONE
        } else if (_uiState.value!!.teamAScore > _uiState.value!!.teamBScore) {
            return TEAM_A
        }
        return TEAM_B
    }

    private fun getNextWord(): String {
        return (allPitkiot - usedWords).random().also { usedWords.add(it) }
    }

    fun onCorrectGuess() {
        if (usedWords.size != allPitkiot.size) {
            val nextWord = getNextWord()
            _uiState.postValue(_uiState.value!!.copy(score = _uiState.value!!.score + 1, curWord = nextWord))
        } else {
            endGame()
        }
    }

    fun getPlayersByTeam(team: Team): List<String> {
        return teamInfo[team]!!.teamPlayers
    }

    private fun resetSkippedWords() {
        usedWords.removeAll(skippedWords)
        skippedWords = mutableSetOf()
    }

    fun onSkipAttempt() {
        if (_uiState.value!!.skipsLeft > 0) {
            usedWords.add(_uiState.value!!.curWord)
            skippedWords.add(_uiState.value!!.curWord)
            if (usedWords.size != allPitkiot.size) {
                val nextWord = getNextWord()
                _uiState.postValue(_uiState.value!!.copy(skipsLeft = _uiState.value!!.skipsLeft - 1, curWord = nextWord))
            } else {
                endGame()
            }
        }
    }

    private fun endGame() {
        setTeamScore(_uiState.value!!.curTeam, _uiState.value!!.score)
        _uiState.postValue(_uiState.value!!.copy(gameEnded = true))
    }

    fun startNewRound() {
        val nextWord = getNextWord()
        viewModelScope.launch {
            _uiState.postValue(
                _uiState.value!!.copy(
                    score = 0,
                    skipsLeft = SKIPS,
                    curWord = nextWord,
                    timeLeftToRound = ROUND_TIME
                )
            )
            roundTimer.start()
        }
    }

    private fun getAndSetPlayers() {
        viewModelScope.launch {
            pitkiotRepository.getPlayers(gamePin).onSuccess { result ->
                allPlayers = result.players
                setTeamInfoMap(allPlayers)
            }.onFailure {
                _uiState.postValue(_uiState.value!!.copy(errorMessage = "Error getting all players of game $gamePin"))
            }
        }
    }

    private fun splitPlayersIntoTeams(players: List<String>): Pair<List<String>, List<String>> {
        val shuffledPlayers = players.shuffled()
        val team1 = shuffledPlayers.subList(0, shuffledPlayers.size / 2)
        val team2 = shuffledPlayers.subList(shuffledPlayers.size / 2, shuffledPlayers.size)
        return Pair(team1, team2)
    }

    private fun setTeamInfoMap(players: List<String>) {
        val (playersTeamA, playersTeamB) = splitPlayersIntoTeams(players)
        teamInfo = mapOf(
            TEAM_A to TeamState(team = TEAM_A, teamPlayers = playersTeamA),
            TEAM_B to TeamState(team = TEAM_B, teamPlayers = playersTeamB)
        )
    }

    private fun TeamState.getNextPlayer(): String {
        curPlayerIndex += 1
        return teamPlayers[curPlayerIndex % teamPlayers.size]
    }

    private fun getNextTeam(): Team = if (_uiState.value!!.curTeam == TEAM_B) TEAM_A else TEAM_B
}