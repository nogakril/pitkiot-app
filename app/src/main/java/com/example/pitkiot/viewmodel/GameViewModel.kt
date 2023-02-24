package com.example.pitkiot.viewmodel

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pitkiot.data.PitkiotRepository
import com.example.pitkiot.data.enums.GameStatus
import com.example.pitkiot.data.models.GameModel
import kotlinx.coroutines.launch

const val ADD_WORDS_TIME: Long = 120000

class GameViewModel(
    private val pitkiotRepository: PitkiotRepository
) : ViewModel() {

    private val _gameInfoLiveData = MutableLiveData<GameModel>()
    val gameInfoLiveData: LiveData<GameModel> = _gameInfoLiveData

    val addWordsTimer = object : CountDownTimer(6000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            _gameInfoLiveData.let {
                it.postValue(it.value!!.updateTimer(millisUntilFinished / 1000))
            }
        }
        override fun onFinish() {
            _gameInfoLiveData.postValue(GameModel(null))
        }
    }

    init {
        viewModelScope.launch {
            _gameInfoLiveData.postValue(GameModel(null))
        }
    }
    
    fun createGame(nickname: String) {
        viewModelScope.launch {
            pitkiotRepository.createGame(nickname).onSuccess { result ->
                _gameInfoLiveData.postValue(GameModel(result.gameId.takeLast(4)))
            }
                .onFailure {
                    TODO()
                }
        }
    }

    fun setGameStatus(status: GameStatus) {
        TODO()
    }
    fun addWordToGame(word: String) {
        println(word) // TODO
    }

    companion object {
        private fun GameModel.updateTimer(time: Long): GameModel {
            return GameModel("id", timeLeftToAddWords = time)
        }
        private fun GameModel.updateGameStatus(status: GameStatus): GameModel {
            return GameModel("id", gameStatus = status)
        }
    }
}