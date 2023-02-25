package com.example.pitkiot.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pitkiot.data.PitkiotRepository
import com.example.pitkiot.data.enums.GameStatus
import com.example.pitkiot.data.models.WaitingRoomUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WaitingRoomViewModel(
    private val pitkiotRepository: PitkiotRepository,
    private val gamePin: String
) : ViewModel() {

    private var checkGameStatusJob: Job? = null
    private var getPlayersJob: Job? = null

    private val _uiState = MutableLiveData<WaitingRoomUiState>()
    val uiState: LiveData<WaitingRoomUiState> = _uiState

    init {
        _uiState.postValue(WaitingRoomUiState())
    }

    fun getPlayers() {
        getPlayersJob = viewModelScope.launch {
            while (true) {
                delay(500)
                pitkiotRepository.getPlayers(gamePin).onSuccess { result ->
                    _uiState.let {
                        it.postValue(it.value!!.copy(players = result.players))
                    }
                }
                    .onFailure {
                        _uiState.let {
                            it.postValue(it.value!!.copy(errorMessage = "Error fetching players of game $gamePin"))
                        }
                    }
            }
        }
    }

    fun setGameStatus(status: GameStatus) {
        viewModelScope.launch {
            pitkiotRepository.setStatus(gamePin, status).onSuccess {
                _uiState.let {
                    it.postValue(it.value!!.copy(gameStatus = status))
                }
            }
                .onFailure {
                    _uiState.let {
                        it.postValue(it.value!!.copy(errorMessage = "Error setting game $gamePin status to $status"))
                    }
                }
        }
    }

    fun checkGameStatus() {
        checkGameStatusJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                getGameStatus()
            }
        }
    }

    suspend fun getGameStatus() {
        pitkiotRepository.getStatus(gamePin).onSuccess { result ->
            _uiState.let {
                it.postValue(
                    it.value!!.copy(
                        gameStatus = when (result.status) {
                            "adding_players" -> GameStatus.ADDING_PLAYERS
                            "adding_words" -> GameStatus.ADDING_WORDS
                            else -> { GameStatus.IN_GAME }
                        }
                    )
                )
            }
        }
            .onFailure {
                _uiState.let {
                    it.postValue(it.value!!.copy(errorMessage = "Error getting game status of game $gamePin"))
                }
            }
    }

    override fun onCleared() {
        super.onCleared()
        checkGameStatusJob?.cancel()
        getPlayersJob?.cancel()
    }
}