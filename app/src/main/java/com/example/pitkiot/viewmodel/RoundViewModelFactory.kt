package com.example.pitkiot.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pitkiot.data.PitkiotApi
import com.example.pitkiot.data.PitkiotRepository

class RoundViewModelFactory(
    private val pitkiotRepositoryFactory: (PitkiotApi) -> PitkiotRepository
) : ViewModelProvider.Factory {
    private val pitkiotApi = PitkiotApi.instance
    private var roundViewModel = RoundViewModel(pitkiotRepositoryFactory.invoke(pitkiotApi))

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return roundViewModel as T
    }
}