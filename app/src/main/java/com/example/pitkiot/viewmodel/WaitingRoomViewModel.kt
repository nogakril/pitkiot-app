package com.example.pitkiot.viewmodel

/* ktlint-disable */
import androidx.lifecycle.*
import com.example.pitkiot.data.PitkiotRepository
/* ktlint-enable */
import com.example.pitkiot.data.PitkiotApi
import com.example.pitkiot.data.PitkiotRepositoryImpl
import com.example.pitkiot.data.enums.GameStatus
import com.example.pitkiot.data.models.WaitingRoomUiState
import kotlinx.coroutines.*

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

    fun getPlayers() {
        getPlayersJob = viewModelScope.launch(defaultDispatcher) {
            val currentPlayers = mutableListOf<String>()
            while (true) {
                delay(500)
                pitkiotRepository.getPlayers(gamePin).onSuccess { result ->
                    val newPlayers = result.players.filter { !currentPlayers.contains(it) }
                    val updatedPlayers = currentPlayers.plus(newPlayers)
                    _uiState.postValue(_uiState.value!!.copy(players = updatedPlayers))
                    currentPlayers.addAll(newPlayers)
                }
                    .onFailure {
                        _uiState.postValue(_uiState.value!!.copy(errorMessage = it.message))
                    }
            }
        }
    }

    fun setGameStatus(status: GameStatus) {
        viewModelScope.launch(defaultDispatcher) {
            pitkiotRepository.setStatus(gamePin, status).onSuccess {
                _uiState.postValue(_uiState.value!!.copy(gameStatus = status))
            }
                .onFailure {
                    _uiState.postValue(_uiState.value!!.copy(errorMessage = it.message))
                }
        }
    }

    fun checkGameStatus() {
        checkGameStatusJob = viewModelScope.launch(defaultDispatcher) {
            while (true) {
                delay(1000)
                getGameStatus()
            }
        }
    }

    suspend fun getGameStatus() {
        pitkiotRepository.getStatus(gamePin).onSuccess { result ->
            _uiState.postValue(_uiState.value!!.copy(gameStatus = GameStatus.fromString(result.status)))
        }
            .onFailure {
                _uiState.postValue(_uiState.value!!.copy(errorMessage = it.message))
            }
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