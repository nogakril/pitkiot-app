package com.example.pitkiot.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pitkiot.data.PitkiotApi
import com.example.pitkiot.data.PitkiotRepository
import com.example.pitkiot.viewmodel.RoundViewModel

class RoundViewModelFactory(
    private val pitkiotRepositoryFactory: (PitkiotApi) -> PitkiotRepository
) : ViewModelProvider.Factory {
    private val pitkiotApi = PitkiotApi.instance
    private var roundViewModel = RoundViewModel("1234", pitkiotRepositoryFactory.invoke(pitkiotApi))

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return roundViewModel as T
    }
}