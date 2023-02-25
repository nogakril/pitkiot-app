package com.example.pitkiot.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pitkiot.data.PitkiotRepository
import com.example.pitkiot.data.enums.GameStatus
import com.example.pitkiot.data.models.AddWordsUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AddWordsViewModel(
    private val pitkiotRepository: PitkiotRepository,
    private val gamePin: String
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
        viewModelScope.launch {
            pitkiotRepository.addWord(gamePin, word).onFailure {
                _uiState.postValue(_uiState.value!!.copy(errorMessage = "Error adding the word $word to game $gamePin"))
            }
        }
    }

    fun setGameStatus(status: GameStatus) {
        viewModelScope.launch {
            pitkiotRepository.setStatus(gamePin, status).onFailure {
                _uiState.postValue(_uiState.value!!.copy(errorMessage = "Error setting game $gamePin status to $status"))
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
            _uiState.postValue(_uiState.value!!.copy(gameStatus = GameStatus.fromString(result.status)))
        }
            .onFailure {
                _uiState.postValue(_uiState.value!!.copy(errorMessage = "Error getting game status of game $gamePin"))
            }
    }

    override fun onCleared() {
        super.onCleared()
        checkGameStatusJob?.cancel()
    }
}