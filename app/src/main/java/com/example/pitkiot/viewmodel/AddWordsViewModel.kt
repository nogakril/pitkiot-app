package com.example.pitkiot.viewmodel

/* ktlint-disable */
import androidx.lifecycle.*
/* ktlint-enable */
import com.example.pitkiot.data.PitkiotApi
import com.example.pitkiot.data.PitkiotRepository
import com.example.pitkiot.data.PitkiotRepositoryImpl
import com.example.pitkiot.data.enums.GameStatus
import com.example.pitkiot.data.models.AddWordsUiState
import kotlinx.coroutines.*
import java.io.IOException

class AddWordsViewModel(
    private val pitkiotRepository: PitkiotRepository,
    private val gamePin: String,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Main
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
        viewModelScope.launch(defaultDispatcher) {
            try {
                pitkiotRepository.addWord(gamePin, word).onFailure {
                    _uiState.postValue(_uiState.value!!.copy(errorMessage = it.message))
                }
            }
            catch (e: IOException){
                _uiState.postValue(_uiState.value!!.copy(errorMessage = "Oops... no internet! Reconnect and try again"))
            }
        }
    }

    fun setGameStatus(status: GameStatus) {
        viewModelScope.launch(defaultDispatcher) {
            try {
                pitkiotRepository.setStatus(gamePin, status).onFailure {
                    _uiState.postValue(_uiState.value!!.copy(errorMessage = it.message))
                }
            }
            catch (e: IOException){
                _uiState.postValue(_uiState.value!!.copy(errorMessage = "Oops... no internet! Reconnect and try again"))
            }
        }
    }

    fun checkGameStatus() {
        checkGameStatusJob = viewModelScope.launch(defaultDispatcher) {
            var firstCall = true
            while (true) {
                delay(1000)
                getGameStatus(firstCall)
                firstCall = false
            }
        }
    }

    suspend fun getGameStatus(firstCall:Boolean) {
        try {
            pitkiotRepository.getStatus(gamePin).onSuccess { result ->
                _uiState.postValue(_uiState.value!!.copy(gameStatus = GameStatus.fromString(result.status)))
            }
                .onFailure {
                    _uiState.postValue(_uiState.value!!.copy(errorMessage = it.message))
                }
        }
        catch (e: IOException) {
            if (firstCall){
                _uiState.postValue(_uiState.value!!.copy(errorMessage = "Oops... no internet! Reconnect and try again"))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        checkGameStatusJob?.cancel()
    }

    class Factory(
        private val pitkiotRepositoryFactory: (PitkiotApi) -> PitkiotRepositoryImpl,
        private val gamePinFactory: () -> String
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val pitkiotApi = PitkiotApi.instance
            return AddWordsViewModel(
                pitkiotRepository = pitkiotRepositoryFactory.invoke(pitkiotApi),
                gamePin = gamePinFactory.invoke()
            ) as T
        }
    }
}