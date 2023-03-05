package com.example.pitkiot.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.pitkiot.FakePitkiotRepository
import com.example.pitkiot.FakeRepositoryState
import com.example.pitkiot.data.enums.GameStatus
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class GameSummaryViewModelTest {
    @get:Rule
    val instantLiveData = InstantTaskExecutorRule()

    private lateinit var viewModel: GameSummaryViewModel

    private val gamePin = "mf7h"

    @Test
    fun onInitViewModel_uiStateLiveDataDefaultValues() = runTest {
        viewModel = GameSummaryViewModel(FakePitkiotRepository(FakeRepositoryState.Success), gamePin, UnconfinedTestDispatcher())
        assertThat(viewModel.uiState.value!!.gamePin).isEqualTo("mf7h")
        assertThat(viewModel.uiState.value!!.errorMessage).isNull()
    }

    @Test
    fun onSetGameStatusValidInput_NothingHappens() = runTest {
        viewModel = GameSummaryViewModel(FakePitkiotRepository(FakeRepositoryState.Success), gamePin, UnconfinedTestDispatcher())
        viewModel.setGameStatus(GameStatus.GAME_ENDED)
        assertThat(viewModel.uiState.value!!.errorMessage).isNull()
    }

    @Test
    fun onSetGameStatusFailure_uiStateLiveDataUpdated() = runTest {
        viewModel = GameSummaryViewModel(FakePitkiotRepository(FakeRepositoryState.Failure), gamePin, UnconfinedTestDispatcher())
        viewModel.setGameStatus(GameStatus.GAME_ENDED)
        assertThat(viewModel.uiState.value!!.errorMessage).isEqualTo("error")
    }

    @Test
    fun onSetGameStatusNoInternet_uiStateLiveDataUpdated() = runTest {
        viewModel = GameSummaryViewModel(FakePitkiotRepository(FakeRepositoryState.NoInternet), gamePin, UnconfinedTestDispatcher())
        viewModel.setGameStatus(GameStatus.GAME_ENDED)
        assertThat(viewModel.uiState.value!!.errorMessage).isEqualTo("Oops... no internet! Reconnect and try again")
    }
}