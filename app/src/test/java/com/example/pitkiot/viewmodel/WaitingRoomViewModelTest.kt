package com.example.pitkiot.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.pitkiot.FakePitkiotRepository
import com.example.pitkiot.FakeRepositoryState
import com.example.pitkiot.data.enums.GameStatus
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.* // ktlint-disable no-wildcard-imports
import kotlinx.coroutines.test.* // ktlint-disable no-wildcard-imports
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class WaitingRoomViewModelTest {
    @get:Rule
    val instantLiveData = InstantTaskExecutorRule()

    private lateinit var viewModel: WaitingRoomViewModel

    private val gamePin = "mf7h"

    @Test
    fun onInitViewModel_uiStateLiveDataDefaultValues() = runTest {
        viewModel = WaitingRoomViewModel(FakePitkiotRepository(FakeRepositoryState.Success), gamePin, UnconfinedTestDispatcher())
        assertThat(viewModel.uiState.value!!.players).isEqualTo(emptyList<String>())
        assertThat(viewModel.uiState.value!!.gameStatus).isEqualTo(GameStatus.ADDING_PLAYERS)
        assertThat(viewModel.uiState.value!!.gamePin).isNull()
        assertThat(viewModel.uiState.value!!.errorMessage).isNull()
    }

    @Test
    fun onCheckPlayersValidInput_uiStateLiveDataUpdated() = runTest {
        viewModel = WaitingRoomViewModel(FakePitkiotRepository(FakeRepositoryState.Success), gamePin, UnconfinedTestDispatcher())
        viewModel.checkPlayers()
        advanceTimeBy(2000)
        assertThat(viewModel.uiState.value!!.players).isEqualTo(listOf("Noga", "Omri", "John", "Mike"))
    }

    @Test
    fun onCheckPlayersFailure_uiStateLiveDataUpdated() = runTest {
        viewModel = WaitingRoomViewModel(FakePitkiotRepository(FakeRepositoryState.Failure), gamePin, UnconfinedTestDispatcher())
        viewModel.checkPlayers()
        advanceTimeBy(2000)
        assertThat(viewModel.uiState.value!!.errorMessage).isEqualTo("error")
    }

    @Test
    fun onCheckPlayersNoInternet_uiStateLiveDataUpdated() = runTest {
        viewModel = WaitingRoomViewModel(FakePitkiotRepository(FakeRepositoryState.NoInternet), gamePin, UnconfinedTestDispatcher())
        viewModel.checkPlayers()
        advanceTimeBy(2000)
        assertThat(viewModel.uiState.value!!.errorMessage).isNotNull()
    }

    @Test
    fun onCheckGameStatusValidInput_uiStateLiveDataUpdated() = runTest {
        viewModel = WaitingRoomViewModel(FakePitkiotRepository(FakeRepositoryState.Success), gamePin, UnconfinedTestDispatcher())
        viewModel.checkGameStatus()
        advanceTimeBy(2000)
        assertThat(viewModel.uiState.value!!.gameStatus).isEqualTo(GameStatus.ADDING_PLAYERS)
    }

    @Test
    fun onCheckGameStatusFailure_uiStateLiveDataUpdated() = runTest {
        viewModel = WaitingRoomViewModel(FakePitkiotRepository(FakeRepositoryState.Failure), gamePin, UnconfinedTestDispatcher())
        viewModel.checkGameStatus()
        advanceTimeBy(2000)
        assertThat(viewModel.uiState.value!!.errorMessage).isEqualTo("error")
    }

    @Test
    fun onCheckGameStatusNoInternet_nothingHappens() = runTest {
        viewModel = WaitingRoomViewModel(FakePitkiotRepository(FakeRepositoryState.NoInternet), gamePin, UnconfinedTestDispatcher())
        viewModel.checkGameStatus()
        advanceTimeBy(2000)
        assertThat(viewModel.uiState.value!!.errorMessage).isNull()
    }

    @Test
    fun onSetGameStatusValidInput_uiStateLiveDataUpdated() = runTest {
        viewModel = WaitingRoomViewModel(FakePitkiotRepository(FakeRepositoryState.Success), gamePin, UnconfinedTestDispatcher())
        viewModel.setGameStatus(GameStatus.ADDING_WORDS)
        assertThat(viewModel.uiState.value!!.gameStatus).isEqualTo(GameStatus.ADDING_WORDS)
    }

    @Test
    fun onSetGameStatusFailure_uiStateLiveDataUpdated() = runTest {
        viewModel = WaitingRoomViewModel(FakePitkiotRepository(FakeRepositoryState.Failure), gamePin, UnconfinedTestDispatcher())
        viewModel.setGameStatus(GameStatus.ADDING_WORDS)
        assertThat(viewModel.uiState.value!!.errorMessage).isEqualTo("error")
    }

    @Test
    fun onSetGameStatusNoInternet_uiStateLiveDataUpdated() = runTest {
        viewModel = WaitingRoomViewModel(FakePitkiotRepository(FakeRepositoryState.NoInternet), gamePin, UnconfinedTestDispatcher())
        viewModel.setGameStatus(GameStatus.ADDING_WORDS)
        assertThat(viewModel.uiState.value!!.errorMessage).isEqualTo("Oops... no internet! Reconnect and try again")
    }
}