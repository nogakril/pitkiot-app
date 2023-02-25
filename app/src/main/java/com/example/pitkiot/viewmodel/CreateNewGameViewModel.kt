package com.example.pitkiot.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pitkiot.data.PitkiotRepository
import com.example.pitkiot.data.models.CreateNewGameUiState
import kotlinx.coroutines.launch

class CreateNewGameViewModel(
    private val pitkiotRepository: PitkiotRepository
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
            _uiState.postValue(_uiState.value!!.copy(errorMessage = "You must choose nickname to create the game"))
            return
        }
        viewModelScope.launch {
            pitkiotRepository.createGame(adminName).onSuccess { result ->
                _uiState.postValue(_uiState.value!!.copy(gamePin = generateGamePin(result.gameId)))
            }
                .onFailure {
                    _uiState.postValue(_uiState.value!!.copy(errorMessage = "Error creating a new game"))
                }
        }
    }
}