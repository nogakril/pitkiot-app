package com.example.pitkiot.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.pitkiot.FakePitkiotRepository
import com.example.pitkiot.FakeRepositoryState
import com.example.pitkiot.data.enums.GameStatus
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class AddWordsViewModelTest {
    @get:Rule
    val instantLiveData = InstantTaskExecutorRule()

    private lateinit var viewModel: AddWordsViewModel

    private val gamePin = "mf7h"

    @Test
    fun onInitViewModel_uiStateLiveDataDefaultValues() = runTest {
        viewModel = AddWordsViewModel(FakePitkiotRepository(FakeRepositoryState.Success), gamePin, UnconfinedTestDispatcher())
        assertThat(viewModel.uiState.value!!.gamePin).isNull()
        assertThat(viewModel.uiState.value!!.errorMessage).isNull()
        assertThat(viewModel.uiState.value!!.gameStatus).isEqualTo(GameStatus.ADDING_WORDS)
    }

    @Test
    fun onAddWordValidInput_nothingHappens() = runTest {
        val word = "hello"
        viewModel = AddWordsViewModel(FakePitkiotRepository(FakeRepositoryState.Success), gamePin, UnconfinedTestDispatcher())
        viewModel.addWord(word)
        assertThat(viewModel.uiState.value?.errorMessage).isNull()
    }

    @Test
    fun onAddWordInvalidInput_uiStateLiveDataUpdated() = runTest {
        val word = ""
        viewModel = AddWordsViewModel(FakePitkiotRepository(FakeRepositoryState.Success), gamePin, UnconfinedTestDispatcher())
        viewModel.addWord(word)
        assertThat(viewModel.uiState.value?.errorMessage).isEqualTo("Game's Pitkit cannot be empty")
    }

    @Test
    fun onAddWordFailure_uiStateLiveDataUpdated() = runTest {
        val word = "hello"
        viewModel = AddWordsViewModel(FakePitkiotRepository(FakeRepositoryState.Failure), gamePin, UnconfinedTestDispatcher())
        viewModel.addWord(word)
        assertThat(viewModel.uiState.value?.errorMessage).isEqualTo("error")
    }

    @Test
    fun onAddWordNoInternet_uiStateLiveDataUpdated() = runTest {
        val word = "hello"
        viewModel = AddWordsViewModel(FakePitkiotRepository(FakeRepositoryState.NoInternet), gamePin, UnconfinedTestDispatcher())
        viewModel.addWord(word)
        assertThat(viewModel.uiState.value?.errorMessage).isEqualTo("Oops... no internet! Reconnect and try again")
    }

    @Test
    fun onCheckGameStatusSuccess_uiStateLiveDataUpdated() = runTest {
        viewModel = AddWordsViewModel(FakePitkiotRepository(FakeRepositoryState.Success), gamePin, UnconfinedTestDispatcher())
        viewModel.checkGameStatus()
        withContext(Dispatchers.Default) {
            delay(5000)
        }
        assertThat(viewModel.uiState.value!!.gameStatus).isEqualTo(GameStatus.ADDING_WORDS)
    }

    @Test
    fun onCheckGameStatusFailure_uiStateLiveDataUpdated() = runTest {
        viewModel = AddWordsViewModel(FakePitkiotRepository(FakeRepositoryState.Failure), gamePin, UnconfinedTestDispatcher())
        viewModel.checkGameStatus()
        withContext(Dispatchers.Default) {
            delay(5000)
        }
        assertThat(viewModel.uiState.value!!.errorMessage).isEqualTo("error")
    }

    @Test
    fun onCheckGameStatusNoInternet_uiStateLiveDataUpdated() = runTest {
        viewModel = AddWordsViewModel(FakePitkiotRepository(FakeRepositoryState.NoInternet), gamePin, UnconfinedTestDispatcher())
        viewModel.checkGameStatus()
        withContext(Dispatchers.Default) {
            delay(5000)
        }
        assertThat(viewModel.uiState.value!!.errorMessage).isEqualTo("Oops... no internet! Reconnect and try again")
    }

    @Test
    fun onSetGameStatusValidInput_uiStateLiveDataUpdated() = runTest {
        viewModel = AddWordsViewModel(FakePitkiotRepository(FakeRepositoryState.Success), gamePin, UnconfinedTestDispatcher())
        viewModel.setGameStatus(GameStatus.IN_GAME)
        assertThat(viewModel.uiState.value!!.gameStatus).isEqualTo(GameStatus.ADDING_WORDS)
    }

    @Test
    fun onSetGameStatusFailure_uiStateLiveDataUpdated() = runTest {
        viewModel = AddWordsViewModel(FakePitkiotRepository(FakeRepositoryState.Failure), gamePin, UnconfinedTestDispatcher())
        viewModel.setGameStatus(GameStatus.IN_GAME)
        assertThat(viewModel.uiState.value!!.errorMessage).isEqualTo("error")
    }

    @Test
    fun onSetGameStatusNoInternet_uiStateLiveDataUpdated() = runTest {
        viewModel = AddWordsViewModel(FakePitkiotRepository(FakeRepositoryState.NoInternet), gamePin, UnconfinedTestDispatcher())
        viewModel.setGameStatus(GameStatus.IN_GAME)
        assertThat(viewModel.uiState.value!!.errorMessage).isEqualTo("Oops... no internet! Reconnect and try again")
    }
}