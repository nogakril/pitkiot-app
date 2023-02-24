package com.example.pitkiot.viewmodel

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pitkiot.data.PitkiotRepository
import com.example.pitkiot.data.enums.Team
import com.example.pitkiot.utils.GameManager
import com.example.pitkiot.utils.GameManagerImpl
import kotlinx.coroutines.launch

const val SKIPS = 2
const val ROUND_TIME: Long = 60000 // milisecs


data class RoundUiState(
    var score: Int = 0,
    var skipsLeft: Int = SKIPS,
    var curWord: String = "",
    var usedWords: Set<String> = emptySet(),
    var timeLeftToRound: Long = ROUND_TIME,
    var curTeam: Team,
    var curPlayer: String,
    val errorMessage: String? = null
)

class RoundViewModel(
    private val gamePin: String,
    private val pitkiotRepository: PitkiotRepository,
    private val gameManager: GameManager
) : ViewModel() {

    private lateinit var allPitkiot: Set<String>
    private lateinit var players: List<String>


    private val _uiState = MutableLiveData<RoundUiState>()
    val uiState: LiveData<RoundUiState> = _uiState

    private val roundTimer = object : CountDownTimer(60000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            _uiState.let {
                it.postValue(it.value!!.copy(timeLeftToRound = millisUntilFinished / 1000))
            }
        }
        override fun onFinish() {
            gameManager.setTeamScore(_uiState.value!!.curTeam, _uiState.value!!.score)
        }
    }

    init {
        viewModelScope.launch {
//            getPlayers(Team.TEAM_A)
//            gameManager.setPlayers(Team.TEAM_A, players)
//            gameManager.setPlayers(Team.TEAM_B, players)
            _uiState.postValue(RoundUiState(curTeam = gameManager.getNextTeam(), curPlayer = gameManager.getNextPlayer()))
            pitkiotRepository.getWords(gamePin).onSuccess { result ->
                allPitkiot = result.words.toSet()
            }.onFailure {
                _uiState.let {
                    it.postValue(it.value!!.copy(errorMessage = "Error getting all pitkiot of game $gamePin"))
                }
            }
        }
    }

    private fun getNextWord(): String {
        val nextWord = (allPitkiot - _uiState.value!!.usedWords).random()
        _uiState.let {
            it.postValue(it.value!!.copy(usedWords = it.value!!.usedWords.plus(nextWord))) // not working
        }
        return nextWord
    }

    fun onCorrectGuess() {
        val nextWord = getNextWord()
        _uiState.let {
            it.postValue(it.value!!.copy(score = it.value!!.score + 1, curWord = nextWord))
        }
    }

    fun onSkipAttempt() {
        if (_uiState.value!!.skipsLeft > 0) {
            _uiState.let {
                it.postValue(it.value!!.copy(skipsLeft = it.value!!.skipsLeft - 1))
            }
        }
    }

    fun startNewRound() {
        val nextWord = getNextWord()
        viewModelScope.launch {
            _uiState.let {
                it.postValue(it.value!!.copy(curWord = nextWord))
            }
            roundTimer.start()
        }
    }

    fun getPlayers(team: Team): List<String> {
        viewModelScope.launch {
            pitkiotRepository.getPlayers(gamePin).onSuccess { result ->
                players = result.players
            }.onFailure {
                _uiState.let {
                    it.postValue(it.value!!.copy(errorMessage = "Error getting all players of game $gamePin"))
                }
            }
        }
    }
}