package com.example.pitkiot

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.pitkiot.data.PitkiotRepository
import com.example.pitkiot.data.enums.GameStatus
import com.example.pitkiot.viewmodel.GameSummaryViewModel
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.junit.MockitoRule
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class GameSummaryViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var mockRepository: PitkiotRepository

    private lateinit var viewModel: GameSummaryViewModel

    private val gamePin = "1234"

    @Before
    fun setUp() {
        viewModel = GameSummaryViewModel(mockRepository, gamePin, UnconfinedTestDispatcher())
    }

    @Test
    fun `setGameStatus updates uiState with GameStatus when repository call is successful`() = runTest {
        val gameStatus = GameStatus.GAME_ENDED
        whenever(mockRepository.setStatus(gamePin, gameStatus)).thenReturn(Result.success(Unit))
        viewModel.setGameStatus(gameStatus)
        assertThat(viewModel.uiState.value?.errorMessage).isNull()
    }

    @Test
    fun `setGameStatus updates uiState with error message when repository call fails`() = runTest {
        val gameStatus = GameStatus.GAME_ENDED
        val exception = Exception("Error setting game status")
        whenever(mockRepository.setStatus(gamePin, gameStatus)).thenReturn(Result.failure(exception))
        viewModel.setGameStatus(gameStatus)
        assertThat(viewModel.uiState.value?.errorMessage).isEqualTo("Error setting game status")
    }
}