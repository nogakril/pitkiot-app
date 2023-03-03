package com.example.pitkiot

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.pitkiot.data.PitkiotRepository
import com.example.pitkiot.viewmodel.JoinGameViewModel
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class JoinGameViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var mockRepository: PitkiotRepository

    private lateinit var viewModel: JoinGameViewModel

    @Before
    fun setUp() {
        viewModel = JoinGameViewModel(mockRepository, UnconfinedTestDispatcher())
    }

    @Test
    fun `joinGame with valid nickname should update uiState gamePin`() = runTest {
        val gamePin = "1234"
        val nickName = "player"
        whenever(mockRepository.addPlayer(gamePin, nickName)).thenReturn(Result.success(Unit))
        viewModel.joinGame(gamePin, nickName)
        verify(mockRepository).addPlayer(gamePin, nickName)
        assertThat(viewModel.uiState.value?.gamePin).isEqualTo(gamePin)
        assertThat(viewModel.uiState.value?.errorMessage).isNull()
    }

    @Test
    fun `joinGame with invalid nickname should update uiState errorMessage`() = runBlockingTest {
        val gamePin = "1234"
        val nickname = ""
        viewModel.joinGame(gamePin, nickname)
        assertThat(viewModel.uiState.value?.errorMessage).isEqualTo("You must choose nickname to create the game")
        assertThat(viewModel.uiState.value?.gamePin).isNull()
        verifyNoMoreInteractions(mockRepository)
    }

    @Test
    fun `joinGame failed call to pitkiotRepository should update uiState errorMessage`() = runTest {
        val gamePin = "1234"
        val nickName = "player"
        val exception = Exception("Error adding player")
        whenever(mockRepository.addPlayer(gamePin, nickName)).thenReturn(Result.failure(exception))
        viewModel.joinGame(gamePin, nickName)
        verify(mockRepository).addPlayer(gamePin, nickName)
        assertThat(viewModel.uiState.value?.errorMessage).isEqualTo("Error adding player")
        assertThat(viewModel.uiState.value?.gamePin).isNull()
    }
}