package com.example.pitkiot.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pitkiot.data.PitkiotRepository

class GameViewModelFactory : ViewModelProvider.Factory {
    private var gameViewModel = GameViewModel(PitkiotRepository())

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return gameViewModel as T
    }
}