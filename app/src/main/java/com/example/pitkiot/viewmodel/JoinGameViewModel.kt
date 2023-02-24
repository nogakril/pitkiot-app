package com.example.pitkiot.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pitkiot.data.PitkiotRepository
import com.example.pitkiot.data.models.JoinGameUiState
import kotlinx.coroutines.launch

class JoinGameViewModel(
    private val pitkiotRepository: PitkiotRepository
) : ViewModel() {

    private val _uiState = MutableLiveData<JoinGameUiState>()
    val uiState: LiveData<JoinGameUiState> = _uiState

    init {
        _uiState.postValue(JoinGameUiState())
    }

    fun joinGame(gamePin: String, nickname: String) {
        val adminName = nickname.trimStart()
        if (adminName == "") {
            _uiState.postValue(_uiState.value!!.copy(errorMessage = "You must choose nickname to create the game"))
            return
        }
        viewModelScope.launch {
            pitkiotRepository.joinGame(gamePin, adminName).onSuccess { result ->
                _uiState.let {
                    it.postValue(it.value!!.copy(gamePin = gamePin))
                }
            }
                .onFailure {
                    _uiState.let {
                        it.postValue(it.value!!.copy(errorMessage = "Error joining game $gamePin"))
                    }
                }
        }
    }
}