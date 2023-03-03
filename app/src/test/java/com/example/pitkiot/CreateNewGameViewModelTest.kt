package com.example.pitkiot

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.pitkiot.data.PitkiotRepository
import com.example.pitkiot.data.models.GameCreationResponse
import com.example.pitkiot.viewmodel.CreateNewGameViewModel
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class CreateNewGameViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var mockRepository: PitkiotRepository

    private lateinit var viewModel: CreateNewGameViewModel

    @Before
    fun setUp() {
        viewModel = CreateNewGameViewModel(mockRepository, UnconfinedTestDispatcher())
    }

    @Test
    fun `createGame with valid nickname should update uiState gamePin`() = runTest {
        val nickName = "admin"
        val gamePin = "1234"
        whenever(mockRepository.createGame(nickName)).thenReturn(Result.success(GameCreationResponse(gameId = gamePin)))
        viewModel.createGame(nickName)
        verify(mockRepository).createGame(nickName)
        assertThat(viewModel.uiState.value?.gamePin).isEqualTo(gamePin)
        assertThat(viewModel.uiState.value?.errorMessage).isNull()
    }

    @Test
    fun `createGame with invalid nickname should update uiState errorMessage`() = runTest {
        viewModel.createGame("")
        assertThat(viewModel.uiState.value?.errorMessage).isEqualTo("You must choose a nickname to create a game")
        assertThat(viewModel.uiState.value?.gamePin).isNull()
    }

    @Test
    fun `createGame failed call to pitkiotRepository should update uiState errorMessage`() = runTest {
        val nickName = "admin"
        val exception = Exception("Error creating a new game")
        whenever(mockRepository.createGame(nickName)).thenReturn(Result.failure(exception))
        viewModel.createGame(nickName)
        verify(mockRepository).createGame(nickName)
        assertThat(viewModel.uiState.value?.errorMessage).isEqualTo("Error creating a new game")
    }
}