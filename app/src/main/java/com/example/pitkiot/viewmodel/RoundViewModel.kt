package com.example.pitkiot.viewmodel

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pitkiot.data.PitkiotRepository
import com.example.pitkiot.data.RoundModel
import kotlinx.coroutines.launch

class RoundViewModel(
    private val pitkiotRepository: PitkiotRepository
) : ViewModel() {

    private val _roundStatsLiveData = MutableLiveData<RoundModel>()
    val roundStatsLiveData: LiveData<RoundModel> = _roundStatsLiveData

    init {
        loadNewRound()
    }

    private fun getNextWord() = pitkiotRepository.getNextWord()

    fun onCorrectGuess() {
        _roundStatsLiveData.postValue(_roundStatsLiveData.value!!.let {
            RoundModel(it.score + 1, it.skipsLeft, getNextWord(), it.countDown)
        })
    }

    fun onSkipAttempt() {
        if (_roundStatsLiveData.value!!.skipsLeft > 0) {
            _roundStatsLiveData.postValue(_roundStatsLiveData.value!!.let {
                RoundModel(it.score, it.skipsLeft - 1, getNextWord(), it.countDown)
            })
        }
    }

    private fun startTimer() {
        val countDownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                _roundStatsLiveData.postValue(_roundStatsLiveData.value!!.let {
                    RoundModel(it.score, it.skipsLeft, it.curWord, millisUntilFinished / 1000)
                })
            }

            override fun onFinish() {
                println("time's up")
            }
        }
        countDownTimer.start()
    }

    private fun loadNewRound() {
        viewModelScope.launch {
            _roundStatsLiveData.postValue(RoundModel())// TODO is this how you do it in kotlin?
            startTimer()
        }
    }
}