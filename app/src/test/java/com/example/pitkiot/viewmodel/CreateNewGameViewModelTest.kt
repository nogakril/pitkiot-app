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
class CreateNewGameViewModelTest {
    @get:Rule
    val instantLiveData = InstantTaskExecutorRule()

    private lateinit var viewModel: CreateNewGameViewModel

    @Test
    fun onInitViewModel_uiStateLiveDataDefaultValues() = runTest {
        viewModel = CreateNewGameViewModel(FakePitkiotRepository(FakeRepositoryState.Success), UnconfinedTestDispatcher())
        assertThat(viewModel.uiState.value!!.gamePin).isNull()
        assertThat(viewModel.uiState.value!!.errorMessage).isNull()
    }

    @Test
    fun onCreateGameValidInput_uiStateLiveDataUpdated() = runTest {
        val nickName = "admin"
        viewModel = CreateNewGameViewModel(FakePitkiotRepository(FakeRepositoryState.Success), UnconfinedTestDispatcher())
        viewModel.createGame(nickName)
        assertThat(viewModel.uiState.value?.gamePin).isEqualTo("mf7h")
    }

    @Test
    fun onCreateGameInvalidInput_uiStateLiveDataUpdated() = runTest {
        val nickName = ""
        viewModel = CreateNewGameViewModel(FakePitkiotRepository(FakeRepositoryState.Success), UnconfinedTestDispatcher())
        viewModel.createGame(nickName)
        assertThat(viewModel.uiState.value?.errorMessage).isEqualTo("You must choose a nickname to create a game")
    }

    @Test
    fun onCreateGameFailure_uiStateLiveDataUpdated() = runTest {
        val nickName = "admin"
        viewModel = CreateNewGameViewModel(FakePitkiotRepository(FakeRepositoryState.Failure), UnconfinedTestDispatcher())
        viewModel.createGame(nickName)
        assertThat(viewModel.uiState.value?.errorMessage).isEqualTo("error")
    }

    @Test
    fun onCreateGameNoInternet_uiStateLiveDataUpdated() = runTest {
        val nickName = "admin"
        viewModel = CreateNewGameViewModel(FakePitkiotRepository(FakeRepositoryState.NoInternet), UnconfinedTestDispatcher())
        viewModel.createGame(nickName)
        assertThat(viewModel.uiState.value?.errorMessage).isEqualTo("Oops... no internet! Reconnect and try again")
    }
}