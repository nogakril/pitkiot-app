package com.example.pitkiot.viewmodel

import androidx.lifecycle.* // ktlint-disable no-wildcard-imports
import com.example.pitkiot.data.PitkiotApi
import com.example.pitkiot.data.PitkiotRepository
import com.example.pitkiot.data.PitkiotRepositoryImpl
import com.example.pitkiot.data.models.CreateNewGameUiState
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

class CreateNewGameViewModel(
    private val pitkiotRepository: PitkiotRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) : ViewModel() {

    private val _uiState = MutableLiveData<CreateNewGameUiState>()
    val uiState: LiveData<CreateNewGameUiState> = _uiState

    init {
        _uiState.postValue(CreateNewGameUiState())
    }

    private fun generateGamePin(gameId: String) = gameId.takeLast(4)

    fun createGame(nickname: String) {
        val adminName = nickname.trimStart()
        if (adminName == "") {
            _uiState.postValue(_uiState.value!!.copy(errorMessage = "You must choose a nickname to create a game"))
            return
        }

        viewModelScope.launch(defaultDispatcher) {
            try {
                pitkiotRepository.createGame(adminName).onSuccess { result ->
                    _uiState.postValue(_uiState.value!!.copy(gamePin = generateGamePin(result.gameId)))
                }
                    .onFailure {
                        _uiState.postValue(_uiState.value!!.copy(errorMessage = it.message))
                    }
            } catch (e: IOException) {
                _uiState.postValue(_uiState.value!!.copy(errorMessage = "Oops... no internet! Reconnect and try again"))
            }
        }
    }

    class Factory(
        private val pitkiotRepositoryFactory: (PitkiotApi) -> PitkiotRepositoryImpl
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val pitkiotApi = PitkiotApi.instance
            return CreateNewGameViewModel(
                pitkiotRepository = pitkiotRepositoryFactory.invoke(pitkiotApi)
            ) as T
        }
    }
}