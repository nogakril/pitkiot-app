package com.example.pitkiot.viewmodel

/* ktlint-disable */
import androidx.lifecycle.*
/* ktlint-enable */
import com.example.pitkiot.data.PitkiotApi
import com.example.pitkiot.data.PitkiotRepository
import com.example.pitkiot.data.PitkiotRepositoryImpl
import com.example.pitkiot.data.enums.GameStatus
import com.example.pitkiot.data.models.AddWordsUiState
import kotlinx.coroutines.*

class AddWordsViewModel(
    private val pitkiotRepository: PitkiotRepository,
    private val gamePin: String,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    private var checkGameStatusJob: Job? = null
    private val _uiState = MutableLiveData<AddWordsUiState>()
    val uiState: LiveData<AddWordsUiState> = _uiState

    init {
        _uiState.postValue(AddWordsUiState())
    }

    fun addWords(curWord: String) {
        val word = curWord.trimStart()
        if (word == "") {
            _uiState.postValue(_uiState.value!!.copy(errorMessage = "Game's Pitkit cannot be empty"))
            return
        }
        viewModelScope.launch(defaultDispatcher) {
            pitkiotRepository.addWord(gamePin, word).onFailure {
                _uiState.postValue(_uiState.value!!.copy(errorMessage = it.message))
            }
        }
    }

    fun setGameStatus(status: GameStatus) {
        viewModelScope.launch(defaultDispatcher) {
            pitkiotRepository.setStatus(gamePin, status).onFailure {
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
    }

    class Factory(
        private val pitkiotRepositoryFactory: (PitkiotApi) -> PitkiotRepositoryImpl,
        private val gamePinFactory: () -> String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val pitkiotApi = PitkiotApi.instance
            return AddWordsViewModel(
                pitkiotRepository = pitkiotRepositoryFactory.invoke(pitkiotApi),
                gamePin = gamePinFactory.invoke()
            ) as T
        }
    }
}