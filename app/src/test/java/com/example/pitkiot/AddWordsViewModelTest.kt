package com.example.pitkiot

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.pitkiot.data.PitkiotRepository
import com.example.pitkiot.data.enums.GameStatus
import com.example.pitkiot.data.models.StatusGetterResponse
import com.example.pitkiot.viewmodel.AddWordsViewModel
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
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
class AddWordsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var mockRepository: PitkiotRepository

    private lateinit var viewModel: AddWordsViewModel

    private val gamePin = "1234"

    @Before
    fun setUp() {
        viewModel = AddWordsViewModel(mockRepository, gamePin, UnconfinedTestDispatcher())
    }

    @Test
    fun `addWords with empty word should update uiState errorMessage`() = runTest {
        viewModel.addWords("   ")
        assertThat(viewModel.uiState.value?.errorMessage).isEqualTo("Game's Pitkit cannot be empty")
    }

    @Test
    fun `addWords with valid word should call addWord from repository with trimmed word`() = runTest {
        val word = " hello "
        whenever(mockRepository.addWord(gamePin, word.trimStart())).thenReturn(Result.success(Unit))
        viewModel.addWords(word)
        verify(mockRepository).addWord(gamePin, word.trimStart())
        assertThat(viewModel.uiState.value?.errorMessage).isNull()
    }

    @Test
    fun `addWords failed call to pitkiotRepository update uiState errorMessage`() = runTest {
        val word = "hello"
        val exception = Exception("Error adding word")
        whenever(mockRepository.addWord(gamePin, word)).thenReturn(Result.failure(exception))
        viewModel.addWords(word)
        verify(mockRepository).addWord(gamePin, word)
        assertThat(viewModel.uiState.value?.errorMessage).isEqualTo("Error adding word")
    }

    @Test
    fun `setGameStatus with valid status should call setStatus from repository`() = runTest {
        val status = GameStatus.IN_GAME
        viewModel.setGameStatus(status)
        verify(mockRepository).setStatus(gamePin, status)
        assertThat(viewModel.uiState.value?.errorMessage).isNull()
    }

    @Test
    fun `setGameStatus failed call to pitkiotRepository update uiState errorMessage`() = runTest {
        val exception = Exception("Error setting status to in_game")
        whenever(mockRepository.setStatus(gamePin, GameStatus.IN_GAME)).thenReturn(Result.failure(exception))
        viewModel.setGameStatus(GameStatus.IN_GAME)
        assertThat(viewModel.uiState.value?.errorMessage).isEqualTo("Error setting status to in_game")
    }

    @Test
    fun `getGameStatus successful call to pitkiotRepository should update gameStatus in uiState`() = runTest {
        val status = GameStatus.IN_GAME
        whenever(mockRepository.getStatus(gamePin)).thenReturn(Result.success(StatusGetterResponse(status.statusName)))
        viewModel.getGameStatus()
        assertThat(viewModel.uiState.value?.gameStatus).isEqualTo(status)
    }

    @Test
    fun `getGameStatus failed call to pitkiotRepository should update uiState errorMessages`() = runTest {
        val exception = Exception("Error getting status")
        whenever(mockRepository.getStatus(gamePin)).thenReturn(Result.failure(exception))
        viewModel.getGameStatus()
        assertThat(viewModel.uiState.value?.errorMessage).isEqualTo("Error getting status")
    }
}