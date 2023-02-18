package com.example.pitkiot.viewmodel

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pitkiot.data.models.GameModel
import com.example.pitkiot.data.enums.GameStatus
import com.example.pitkiot.data.PitkiotRepository
import com.example.pitkiot.data.models.RoundModel
import kotlinx.coroutines.launch

const val ADD_WORDS_TIME: Long = 120000

class GameViewModel(
    private val pitkiotRepository: PitkiotRepository
) : ViewModel() {

    private val _roundInfoLiveData = MutableLiveData<RoundModel>()
    val roundInfoLiveData: LiveData<RoundModel> = _roundInfoLiveData

    private val _gameInfoLiveData = MutableLiveData<GameModel>()
    val gameInfoLiveData: LiveData<GameModel> = _gameInfoLiveData

    val addWordsTimer = object : CountDownTimer(6000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            _gameInfoLiveData.let {
                it.postValue(it.value!!.updateTimer(millisUntilFinished / 1000))
            }
        }
        override fun onFinish() {
            _gameInfoLiveData.postValue(GameModel(GameStatus.GAME))
        }
    }

    private val roundTimer = object : CountDownTimer(60000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            _roundInfoLiveData.let {
                it.postValue(it.value!!.updateTimer(millisUntilFinished / 1000))
            }
        }
        override fun onFinish() {
            println("time's up")
        }
    }

    init {
        viewModelScope.launch {
            _gameInfoLiveData.postValue(GameModel())
        }
    }

    private fun getNextWord() = pitkiotRepository.getNextWord()

    fun onCorrectGuess() {
        _roundInfoLiveData.let {
            it.postValue(it.value!!.incrementScoreAndGetNextWord(getNextWord()))
        }
    }
    fun onSkipAttempt() {
        if (_roundInfoLiveData.value!!.skipsLeft > 0) {
            _roundInfoLiveData.let {
                it.postValue(it.value!!.reduceSkipsIfLeftAndGetNextWord(getNextWord()))
            }
        }
    }
    fun setGameStatus(status: GameStatus) {
        viewModelScope.launch {
            _gameInfoLiveData.let {
                it.postValue(GameModel(status))
            }
        }
    }

    fun startNewRound() {
        viewModelScope.launch {
            _roundInfoLiveData.postValue(RoundModel(curWord = getNextWord())) // TODO is this how you do it in kotlin?
            roundTimer.start()
        }
    }

    fun addWordToGame(word: String) {
        println(word) // TODO
    }

    companion object {
        private fun RoundModel.incrementScoreAndGetNextWord(nextWord: String): RoundModel {
            return RoundModel(score + 1, skipsLeft, nextWord, timeLeftToRound)
        }
        private fun RoundModel.reduceSkipsIfLeftAndGetNextWord(nextWord: String): RoundModel {
            if (skipsLeft > 0) {
                return RoundModel(score, skipsLeft - 1, nextWord, timeLeftToRound)
            }
            return this
        }
        private fun RoundModel.updateTimer(time: Long): RoundModel {
            return RoundModel(score, skipsLeft, curWord, time)
        }
        private fun GameModel.updateTimer(time: Long): GameModel {
            return GameModel(gameStatus, time)
        }
        private fun GameModel.updateGameStatus(status: GameStatus): GameModel {
            return GameModel(status, timeLeftToAddWords)
        }
    }
}