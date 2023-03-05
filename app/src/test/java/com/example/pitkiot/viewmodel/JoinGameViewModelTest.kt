package com.example.pitkiot.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.pitkiot.FakePitkiotRepository
import com.example.pitkiot.FakeRepositoryState
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
class JoinGameViewModelTest {
    @get:Rule
    val instantLiveData = InstantTaskExecutorRule()

    private lateinit var viewModel: JoinGameViewModel

    @Test
    fun onInitViewModel_uiStateLiveDataDefaultValues() = runTest {
        viewModel = JoinGameViewModel(FakePitkiotRepository(FakeRepositoryState.Success), UnconfinedTestDispatcher())
        assertThat(viewModel.uiState.value!!.gamePin).isNull()
        assertThat(viewModel.uiState.value!!.errorMessage).isNull()
    }

    @Test
    fun onJoinGameValidInput_uiStateLiveDataUpdated() = runTest {
        val nickName = "player"
        val gamePin = "mf7h"
        viewModel = JoinGameViewModel(FakePitkiotRepository(FakeRepositoryState.Success), UnconfinedTestDispatcher())
        viewModel.joinGame(gamePin, nickName)
        assertThat(viewModel.uiState.value?.gamePin).isEqualTo("mf7h")
    }

    @Test
    fun onJoinGameInvalidInput_uiStateLiveDataUpdated() = runTest {
        val nickName = " "
        val gamePin = "mf7h"
        viewModel = JoinGameViewModel(FakePitkiotRepository(FakeRepositoryState.Success), UnconfinedTestDispatcher())
        viewModel.joinGame(gamePin, nickName)
        assertThat(viewModel.uiState.value?.errorMessage).isNotNull()
    }

    @Test
    fun onJoinGameFailure_uiStateLiveDataUpdated() = runTest {
        val nickName = "player"
        val gamePin = "mf7h"
        viewModel = JoinGameViewModel(FakePitkiotRepository(FakeRepositoryState.Failure), UnconfinedTestDispatcher())
        viewModel.joinGame(gamePin, nickName)
        assertThat(viewModel.uiState.value?.errorMessage).isNotNull()
    }

    @Test
    fun onJoinGameNoInternet_uiStateLiveDataUpdated() = runTest {
        val nickName = "player"
        val gamePin = "mf7h"
        viewModel = JoinGameViewModel(FakePitkiotRepository(FakeRepositoryState.NoInternet), UnconfinedTestDispatcher())
        viewModel.joinGame(gamePin, nickName)
        assertThat(viewModel.uiState.value?.errorMessage).isNotNull()
    }
}