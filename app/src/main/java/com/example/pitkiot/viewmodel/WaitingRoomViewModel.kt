package com.example.pitkiot.viewmodel

import androidx.lifecycle.* // ktlint-disable no-wildcard-imports
import com.example.pitkiot.data.PitkiotApi
import com.example.pitkiot.data.PitkiotRepository
import com.example.pitkiot.data.PitkiotRepositoryImpl
import com.example.pitkiot.data.enums.GameStatus
import com.example.pitkiot.data.models.WaitingRoomUiState
import kotlinx.coroutines.* // ktlint-disable no-wildcard-imports
import java.io.IOException

class WaitingRoomViewModel(
    private val pitkiotRepository: PitkiotRepository,
    private val gamePin: String,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    private var checkGameStatusJob: Job? = null
    private var getPlayersJob: Job? = null

    private val _uiState = MutableLiveData<WaitingRoomUiState>()
    val uiState: LiveData<WaitingRoomUiState> = _uiState

    init {
        _uiState.postValue(WaitingRoomUiState())
    }

    fun checkPlayers() {
        getPlayersJob = viewModelScope.launch(Dispatchers.IO) {
            val currentPlayers = mutableListOf<String>()
            var firstCall = true
            while (true) {
                delay(500)
                getPlayers(firstCall, currentPlayers)
                if (firstCall) {
                    firstCall = false
                }
            }
        }
    }

    private suspend fun getPlayers(firstCall: Boolean, currentPlayers: MutableList<String>) {
        try {
            pitkiotRepository.getPlayers(gamePin).onSuccess { result ->
                val newPlayers = result.players.filter { !currentPlayers.contains(it) }
                val updatedPlayers = currentPlayers.plus(newPlayers)
                _uiState.postValue(_uiState.value!!.copy(players = updatedPlayers))
                currentPlayers.addAll(newPlayers)
            }
                .onFailure {
                    _uiState.postValue(_uiState.value!!.copy(errorMessage = it.message))
                }
        } catch (e: java.lang.Exception) {
            if (firstCall) {
                _uiState.postValue(_uiState.value!!.copy(errorMessage = "Oops... no internet! Reconnect and try again"))
            }
        }
    }

    fun setGameStatus(status: GameStatus) {
        viewModelScope.launch(defaultDispatcher) {
            try {
                pitkiotRepository.setStatus(gamePin, status).onSuccess {
                    _uiState.postValue(_uiState.value!!.copy(gameStatus = status))
                }
                    .onFailure {
                        _uiState.postValue(_uiState.value!!.copy(errorMessage = it.message))
                    }
            } catch (e: IOException) {
                _uiState.postValue(_uiState.value!!.copy(errorMessage = "Oops... no internet! Reconnect and try again"))
            }
        }
    }

    fun checkGameStatus() {
        checkGameStatusJob = viewModelScope.launch(defaultDispatcher) {
            while (true) {
                delay(500)
                getGameStatus()
            }
        }
    }

    private suspend fun getGameStatus() {
        try {
            pitkiotRepository.getStatus(gamePin).onSuccess { result ->
                _uiState.postValue(_uiState.value!!.copy(gameStatus = GameStatus.fromString(result.status)))
            }
                .onFailure {
                    _uiState.postValue(_uiState.value!!.copy(errorMessage = it.message))
                }
        } catch (_: IOException) { }
    }

    override fun onCleared() {
        super.onCleared()
        checkGameStatusJob?.cancel()
        getPlayersJob?.cancel()
    }

    class Factory(
        private val pitkiotRepositoryFactory: (PitkiotApi) -> PitkiotRepositoryImpl,
        private val gamePinFactory: () -> String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val pitkiotApi = PitkiotApi.instance
            return WaitingRoomViewModel(
                pitkiotRepository = pitkiotRepositoryFactory.invoke(pitkiotApi),
                gamePin = gamePinFactory.invoke()
            ) as T
        }
    }
}