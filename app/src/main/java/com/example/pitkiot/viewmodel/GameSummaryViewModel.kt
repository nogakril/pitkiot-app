package com.example.pitkiot.viewmodel

import androidx.lifecycle.* // ktlint-disable no-wildcard-imports
import com.example.pitkiot.data.PitkiotApi
import com.example.pitkiot.data.PitkiotRepository
import com.example.pitkiot.data.PitkiotRepositoryImpl
import com.example.pitkiot.data.enums.GameStatus
import com.example.pitkiot.data.models.GameSummaryUiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

class GameSummaryViewModel(
    private val pitkiotRepository: PitkiotRepository,
    private val gamePin: String,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    private val _uiState = MutableLiveData<GameSummaryUiState>()
    val uiState: LiveData<GameSummaryUiState> = _uiState

    init {
        _uiState.postValue(GameSummaryUiState(gamePin = gamePin))
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

    class Factory(
        private val pitkiotRepositoryFactory: (PitkiotApi) -> PitkiotRepositoryImpl,
        private val gamePinFactory: () -> String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val pitkiotApi = PitkiotApi.instance
            return GameSummaryViewModel(
                pitkiotRepository = pitkiotRepositoryFactory.invoke(pitkiotApi),
                gamePin = gamePinFactory.invoke()
            ) as T
        }
    }

    companion object {
        const val NO_INTERNET_ERROR_MESSAGE = "Oops... no internet! Reconnect and try again"
    }
}