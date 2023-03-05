package com.example.pitkiot.viewmodel

import android.os.CountDownTimer
import androidx.lifecycle.* // ktlint-disable no-wildcard-imports
import androidx.savedstate.SavedStateRegistryOwner
import com.example.pitkiot.data.PitkiotApi
import com.example.pitkiot.data.PitkiotRepository
import com.example.pitkiot.data.PitkiotRepositoryImpl
import com.example.pitkiot.data.enums.Team
import com.example.pitkiot.data.enums.Team.* // ktlint-disable no-wildcard-imports
import com.example.pitkiot.data.models.RoundUiState
import kotlinx.coroutines.* // ktlint-disable no-wildcard-imports
import java.io.IOException

const val SKIPS = 2
const val ROUND_TIME: Long = 60000

class RoundViewModel(
    private val gamePin: String,
    private val pitkiotRepository: PitkiotRepository,
    private val state: SavedStateHandle,
    defaultDispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    private lateinit var allPitkiot: Set<String>
    private lateinit var allPlayers: List<String>
    private lateinit var playersTeamA: List<String>
    private lateinit var playersTeamB: List<String>

    private val _uiState = state.getLiveData<RoundUiState>("liveData")
    val uiState: LiveData<RoundUiState> = _uiState

    private fun startRoundTimer(roundTime: Long = ROUND_TIME) {
        object : CountDownTimer(roundTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _uiState.postValue(_uiState.value!!.copy(timeLeftToRound = millisUntilFinished / 1000))
            }
            override fun onFinish() {
                if (wordsLeft()) {
                    val usedWords = _uiState.value!!.usedWords
                    usedWords.remove(_uiState.value!!.curWord)
                    usedWords.removeAll(_uiState.value!!.skippedWords)
                    val nextTeam = getNextTeam()
                    _uiState.postValue(
                        _uiState.value!!.copy(
                            curTeam = nextTeam,
                            curPlayer = getNextPlayer(),
                            playerIndexTeamA = getPlayerIndexTeamA(),
                            teamAScore = getScoreTeamA(),
                            playerIndexTeamB = getPlayerIndexTeamB(),
                            teamBScore = getScoreTeamB(),
                            inRound = false,
                            usedWords = usedWords,
                            skippedWords = mutableSetOf()
                        )
                    )
                } else {
                    endGame()
                }
            }
        }.start()
    }

    init {
        viewModelScope.launch(defaultDispatcher) {
            if (state.contains("liveData")) {
                _uiState.postValue(state["liveData"])
                if (_uiState.value!!.inRound) {
                    startRoundTimer(_uiState.value!!.timeLeftToRound * 1000)
                }
            } else {
                getAndSetPlayers()
                initGame()
                _uiState.postValue(_uiState.value!!.copy(allPitkiot = allPitkiot, showTeamsDivisionDialog = true))
            }
        }
    }

    private suspend fun initGame() =
        withContext(Dispatchers.IO) {
            _uiState.postValue(
                RoundUiState(
                    curTeam = TEAM_A,
                    curPlayer = playersTeamA[0],
                    allPlayers = allPlayers,
                    playersTeamA = playersTeamA,
                    playersTeamB = playersTeamB
                )
            )
            try {
                pitkiotRepository.getWords(gamePin).onSuccess { result ->
                    allPitkiot = result.words.toSet()
                    _uiState.postValue(_uiState.value!!.copy(showStartBtn = true))
                }.onFailure {
                    _uiState.postValue(_uiState.value!!.copy(errorMessage = it.message))
                }
            } catch (e: IOException) {
                _uiState.postValue(_uiState.value!!.copy(errorMessage = "Oops... no internet! Reconnect and try again"))
            }
        }

    fun onGameEndedReturnWinner(): Team =
        when {
            _uiState.value!!.teamAScore == _uiState.value!!.teamBScore -> NONE
            _uiState.value!!.teamAScore > _uiState.value!!.teamBScore -> TEAM_A
            else -> TEAM_B
        }

    fun onCorrectGuess(): Boolean {
        if (wordsLeft()) {
            val nextWord = getNextWord()
            val curUsedWords = _uiState.value!!.usedWords
            curUsedWords.add(_uiState.value!!.curWord)
            _uiState.postValue(
                _uiState.value!!.copy(
                    score = _uiState.value!!.score + 1,
                    curWord = nextWord,
                    usedWords = curUsedWords
                )
            )
        } else {
            endGame()
        }
        return true
    }

    fun getPlayersByTeam(team: Team): List<String> {
        return when (team) {
            TEAM_A -> _uiState.value!!.playersTeamA
            TEAM_B -> _uiState.value!!.playersTeamB
            else -> emptyList()
        }
    }

    fun onSkipAttempt(): Boolean {
        if (_uiState.value!!.skipsLeft > 0) {
            val skippedWords = _uiState.value!!.skippedWords
            val usedWords = _uiState.value!!.usedWords
            skippedWords.add(_uiState.value!!.curWord)
            usedWords.add(_uiState.value!!.curWord)
            if (wordsLeft()) {
                val nextWord = getNextWord()
                _uiState.postValue(
                    _uiState.value!!.copy(
                        skipsLeft = _uiState.value!!.skipsLeft - 1,
                        curWord = nextWord,
                        skippedWords = skippedWords,
                        usedWords = usedWords
                    )
                )
            } else {
                endGame()
            }
            return true
        }
        return false
    }

    fun startNewRound() {
        val nextWord = getNextWord()
        _uiState.postValue(
            _uiState.value!!.copy(
                score = 0,
                skipsLeft = SKIPS,
                curWord = nextWord,
                timeLeftToRound = ROUND_TIME,
                inRound = true
            )
        )
        startRoundTimer()
    }

    private fun endGame() {
        if (_uiState.value!!.curTeam == TEAM_A) {
            _uiState.postValue(_uiState.value!!.copy(gameEnded = true, teamAScore = _uiState.value!!.teamAScore + _uiState.value!!.score + 1))
        } else {
            _uiState.postValue(_uiState.value!!.copy(gameEnded = true, teamBScore = _uiState.value!!.teamBScore + _uiState.value!!.score + 1))
        }
    }

    private suspend fun getAndSetPlayers() =
        withContext(Dispatchers.IO) {
            var isInitialized = false
            while (!isInitialized) {
                try {
                    pitkiotRepository.getPlayers(gamePin).onSuccess { result ->
                        allPlayers = result.players
                        val shuffledPlayers = allPlayers.shuffled()
                        playersTeamA = (shuffledPlayers.subList(0, shuffledPlayers.size / 2)).toList()
                        playersTeamB = shuffledPlayers.subList(shuffledPlayers.size / 2, shuffledPlayers.size).toList()
                        isInitialized = true
                    }.onFailure {
                        _uiState.postValue(_uiState.value!!.copy(errorMessage = it.message))
                    }
                } catch (_: IOException) { }
            }
        }

    private fun getNextWord(): String {
        return (_uiState.value!!.allPitkiot - _uiState.value!!.usedWords - _uiState.value!!.curWord).random()
    }

    private fun getNextTeam(): Team = if (_uiState.value!!.curTeam == TEAM_B) TEAM_A else TEAM_B

    private fun getPlayerIndexTeamA(): Int {
        return if (_uiState.value!!.curTeam == TEAM_A) {
            (_uiState.value!!.playerIndexTeamA + 1) % _uiState.value!!.playersTeamA.size
        } else {
            _uiState.value!!.playerIndexTeamA
        }
    }

    private fun getScoreTeamA(): Int {
        return if (_uiState.value!!.curTeam == TEAM_A) {
            _uiState.value!!.teamAScore + _uiState.value!!.score
        } else {
            _uiState.value!!.teamAScore
        }
    }

    private fun getPlayerIndexTeamB(): Int {
        return if (_uiState.value!!.curTeam == TEAM_B) {
            (_uiState.value!!.playerIndexTeamB + 1) % _uiState.value!!.playersTeamB.size
        } else {
            _uiState.value!!.playerIndexTeamB
        }
    }

    private fun getScoreTeamB(): Int {
        return if (_uiState.value!!.curTeam == TEAM_B) {
            _uiState.value!!.teamBScore + _uiState.value!!.score
        } else {
            _uiState.value!!.teamBScore
        }
    }

    private fun getNextPlayer(): String {
        return if (_uiState.value!!.curTeam == TEAM_A) {
            _uiState.value!!.playersTeamB[_uiState.value!!.playerIndexTeamB]
        } else {
            _uiState.value!!.playersTeamA[_uiState.value!!.playerIndexTeamA]
        }
    }

    private fun wordsLeft() = _uiState.value!!.usedWords.size + 1 < _uiState.value!!.allPitkiot.size

    class Factory(
        private val pitkiotRepositoryFactory: (PitkiotApi) -> PitkiotRepositoryImpl,
        private val gamePinFactory: () -> String,
        owner: SavedStateRegistryOwner
    ) : AbstractSavedStateViewModelFactory(owner, null) {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(
            key: String,
            modelClass: Class<T>,
            state: SavedStateHandle
        ): T {
            val pitkiotApi = PitkiotApi.instance
            return RoundViewModel(
                gamePin = gamePinFactory.invoke(),
                pitkiotRepository = pitkiotRepositoryFactory.invoke(pitkiotApi),
                state = state
            ) as T
        }
    }
}