package com.example.pitkiot.viewmodel

import androidx.lifecycle.* // ktlint-disable no-wildcard-imports
import com.example.pitkiot.data.PitkiotApi
import com.example.pitkiot.data.PitkiotRepository
import com.example.pitkiot.data.PitkiotRepositoryImpl
import com.example.pitkiot.data.enums.GameStatus
import com.example.pitkiot.data.models.AddWordsUiState
import kotlinx.coroutines.* // ktlint-disable no-wildcard-imports
import java.io.IOException

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

    fun addWord(curWord: String) {
        val word = curWord.trimStart().trimEnd()

        if (word == "") {
            _uiState.postValue(_uiState.value!!.copy(errorMessage = EMPTY_WORD_ERROR_MESSAGE))
            return
        }
        viewModelScope.launch(defaultDispatcher) {
            try {
                pitkiotRepository.addWord(gamePin, word).onFailure {
                    _uiState.postValue(_uiState.value!!.copy(errorMessage = it.message))
                }
            } catch (e: IOException) {
                _uiState.postValue(_uiState.value!!.copy(errorMessage = NO_INTERNET_ERROR_MESSAGE))
            }
        }
    }

    fun setGameStatus(status: GameStatus) {
        viewModelScope.launch(defaultDispatcher) {
            try {
                pitkiotRepository.setStatus(gamePin, status).onFailure {
                    _uiState.postValue(_uiState.value!!.copy(errorMessage = it.message))
                }
            } catch (e: IOException) {
                _uiState.postValue(_uiState.value!!.copy(errorMessage = NO_INTERNET_ERROR_MESSAGE))
            }
        }
    }

    fun checkGameStatus() {
        checkGameStatusJob = viewModelScope.launch(defaultDispatcher) {
            var firstCall = true
            while (true) {
                getGameStatus(firstCall)
                delay(STATUS_CHECK_DELAY)
                firstCall = false
            }
        }
    }

    private suspend fun getGameStatus(firstCall: Boolean) {
        try {
            pitkiotRepository.getStatus(gamePin).onSuccess { result ->
                _uiState.postValue(_uiState.value!!.copy(gameStatus = GameStatus.fromString(result.status)))
            }
                .onFailure {
                    _uiState.postValue(_uiState.value!!.copy(errorMessage = it.message))
                }
        } catch (e: IOException) {
            if (firstCall) {
                _uiState.postValue(_uiState.value!!.copy(errorMessage = NO_INTERNET_ERROR_MESSAGE))
            }
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

    companion object {
        const val NO_INTERNET_ERROR_MESSAGE = "Oops... no internet! Reconnect and try again"
        const val EMPTY_WORD_ERROR_MESSAGE = "Game's Pitkit cannot be empty"
        const val STATUS_CHECK_DELAY = 500L
    }
}