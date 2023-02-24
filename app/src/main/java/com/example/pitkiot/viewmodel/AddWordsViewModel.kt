package com.example.pitkiot.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pitkiot.data.PitkiotRepository
import com.example.pitkiot.data.enums.GameStatus
import kotlinx.coroutines.launch

data class AddWordsUiState(
    val gamePin: String? = null,
    val gameStatus: GameStatus = GameStatus.PITKIOT_CREATION,
    val errorMessage: String? = null
)

class AddWordsViewModel(
    private val pitkiotRepository: PitkiotRepository,
    private val gamePin: String
) : ViewModel() {

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
                _uiState.let {
                    it.postValue(it.value!!.copy(errorMessage = "Error adding the word $word to game $gamePin"))
                }
            }
        }
    }

    fun setGameStatus(status: GameStatus) {
        viewModelScope.launch {
            pitkiotRepository.setStatus(gamePin).onSuccess {
                _uiState.let {
                    it.postValue(it.value!!.copy(gameStatus = status))
                }
            }
                .onFailure {
                    _uiState.let {
                        it.postValue(it.value!!.copy(errorMessage = "Error setting game $gamePin status to $status"))
                    }
                }
        }
    }
}