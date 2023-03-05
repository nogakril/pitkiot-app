package com.example.pitkiot.viewmodel

import androidx.lifecycle.* // ktlint-disable no-wildcard-imports
import com.example.pitkiot.data.PitkiotApi
import com.example.pitkiot.data.PitkiotRepository
import com.example.pitkiot.data.PitkiotRepositoryImpl
import com.example.pitkiot.data.models.JoinGameUiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

class JoinGameViewModel(
    private val pitkiotRepositoryImpl: PitkiotRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    private val _uiState = MutableLiveData<JoinGameUiState>()
    val uiState: LiveData<JoinGameUiState> = _uiState

    init {
        _uiState.postValue(JoinGameUiState())
    }

    fun joinGame(gamePin: String, nickname: String) {
        val adminName = nickname.trimStart().trimEnd()
        if (adminName == "") {
            _uiState.postValue(_uiState.value!!.copy(errorMessage = EMPTY_NICKNAME_ERROR_MESSAGE))
            return
        }
        viewModelScope.launch(defaultDispatcher) {
            try {
                pitkiotRepositoryImpl.addPlayer(gamePin, adminName).onSuccess { result ->
                    _uiState.postValue(_uiState.value!!.copy(gamePin = gamePin))
                }
                    .onFailure {
                        _uiState.postValue(_uiState.value!!.copy(errorMessage = it.message))
                    }
            } catch (e: IOException) {
                _uiState.postValue(_uiState.value!!.copy(errorMessage = NO_INTERNET_ERROR_MESSAGE))
            }
        }
    }

    class Factory(
        private val pitkiotRepositoryFactory: (PitkiotApi) -> PitkiotRepositoryImpl
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val pitkiotApi = PitkiotApi.instance
            return JoinGameViewModel(
                pitkiotRepositoryImpl = pitkiotRepositoryFactory.invoke(pitkiotApi)
            ) as T
        }
    }

    companion object {
        const val NO_INTERNET_ERROR_MESSAGE = "Oops... no internet! Reconnect and try again"
        const val EMPTY_NICKNAME_ERROR_MESSAGE = "You must choose a nickname to join a game"
    }
}