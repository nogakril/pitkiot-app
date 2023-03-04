package com.example.pitkiot.viewmodel

/* ktlint-disable */
import kotlinx.coroutines.*
/* ktlint-enable */
import android.os.CountDownTimer
import androidx.lifecycle.*
import com.example.pitkiot.data.PitkiotRepository
import com.example.pitkiot.data.PitkiotApi
import com.example.pitkiot.data.PitkiotRepositoryImpl
import com.example.pitkiot.data.enums.GameStatus.GAME_ENDED
import com.example.pitkiot.data.enums.Team
import com.example.pitkiot.data.enums.Team.TEAM_A
import com.example.pitkiot.data.enums.Team.TEAM_B
import com.example.pitkiot.data.enums.Team.NONE
import com.example.pitkiot.data.models.RoundUiState
import com.example.pitkiot.data.models.TeamState
import java.io.IOException

const val SKIPS = 2
const val ROUND_TIME: Long = 10000 // milisecs

class RoundViewModel(
    private val gamePin: String,
    private val pitkiotRepository: PitkiotRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Main
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
            if (usedWords.size < allPitkiot.size) {
                usedWords.remove(_uiState.value!!.curWord)
                setTeamScore(_uiState.value!!.curTeam, _uiState.value!!.score)
                val nextTeam = getNextTeam()
                _uiState.postValue(
                    _uiState.value!!.copy(
                        curTeam = nextTeam,
                        curPlayer = teamInfo[nextTeam]!!.getNextPlayer(),
                        inRound = false
                    )
                )
            } else {
                endGame()
            }
        }
    }

    init {
        viewModelScope.launch(defaultDispatcher) {
//            val getAndSetPlayersJob = getAndSetPlayers()
//            getAndSetPlayersJob.join()
            getAndSetPlayers()
            initGame()
//            val initGameJob = initGame()
//            initGameJob.join()
            _uiState.postValue(_uiState.value!!.copy(showTeamsDivisionDialog = true))
        }
    }

    private suspend fun initGame() =
         withContext(Dispatchers.IO) {
            _uiState.postValue(RoundUiState(curTeam = TEAM_A, curPlayer = teamInfo[TEAM_A]!!.getNextPlayer()))
             try {
                 pitkiotRepository.getWords(gamePin).onSuccess { result ->
                     allPitkiot = result.words.toSet()
                     _uiState.postValue(_uiState.value!!.copy(showStartBtn = true))
                 }.onFailure {
                     _uiState.postValue(_uiState.value!!.copy(errorMessage = it.message))
                 }
             }
             catch (e: IOException){
                 _uiState.postValue(_uiState.value!!.copy(errorMessage = "Oops... no internet! Reconnect and try again"))
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

    fun onCorrectGuess(): Boolean {
        if (usedWords.size < allPitkiot.size) {
            val nextWord = getNextWord()
            _uiState.postValue(_uiState.value!!.copy(score = _uiState.value!!.score + 1, curWord = nextWord))
        } else {
            endGame()
        }
        return true
    }

    fun getPlayersByTeam(team: Team): List<String> {
        return teamInfo[team]!!.teamPlayers
    }

    private fun resetSkippedWords() {
        usedWords.removeAll(skippedWords)
        skippedWords = mutableSetOf()
    }

    fun onSkipAttempt(): Boolean {
        if (_uiState.value!!.skipsLeft > 0) {
            usedWords.add(_uiState.value!!.curWord)
            skippedWords.add(_uiState.value!!.curWord)
            if (usedWords.size < allPitkiot.size) {
                val nextWord = getNextWord()
                _uiState.postValue(_uiState.value!!.copy(skipsLeft = _uiState.value!!.skipsLeft - 1, curWord = nextWord))
            } else {
                endGame()
            }
            return true
        }
        return false
    }

    private fun endGame() {
        setTeamScore(_uiState.value!!.curTeam, _uiState.value!!.score + 1)
        viewModelScope.launch(defaultDispatcher) {
            try {
                pitkiotRepository.setStatus(gamePin, GAME_ENDED).onFailure {
                    _uiState.postValue(_uiState.value!!.copy(errorMessage = it.message))
                }
            }
            catch (e: IOException){
                _uiState.postValue(_uiState.value!!.copy(errorMessage = "Oops... no internet! Reconnect and try again"))
            }
        }
        _uiState.postValue(_uiState.value!!.copy(gameEnded = true))
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
        roundTimer.start()
    }

    private suspend fun getAndSetPlayers() =
        withContext(Dispatchers.IO) {
            var isInitialized = false
            while (!isInitialized) {
                try {
                    pitkiotRepository.getPlayers(gamePin).onSuccess { result ->
                        allPlayers = result.players
                        setTeamInfoMap(allPlayers)
                        isInitialized = true
                    }.onFailure {
                        _uiState.postValue(_uiState.value!!.copy(errorMessage = it.message))
                    }
                } catch (_: IOException) {}
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

    class Factory(
        private val pitkiotRepositoryFactory: (PitkiotApi) -> PitkiotRepositoryImpl,
        private val gamePinFactory: () -> String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val pitkiotApi = PitkiotApi.instance
            return RoundViewModel(
                gamePin = gamePinFactory.invoke(),
                pitkiotRepository = pitkiotRepositoryFactory.invoke(pitkiotApi)
            ) as T
        }
    }
}