package com.example.pitkiot.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.example.pitkiot.FakePitkiotRepository
import com.example.pitkiot.FakeRepositoryState
import com.example.pitkiot.data.enums.Team
import com.example.pitkiot.data.models.* // ktlint-disable no-wildcard-imports
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.* // ktlint-disable no-wildcard-imports
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class RoundViewModelTest {

    @get:Rule
    val instantLiveData = InstantTaskExecutorRule()

    private lateinit var viewModel: RoundViewModel

    private val gamePin = "mf7h"

    private val state = SavedStateHandle()

    @Test
    fun onInitialization_uiStateLiveDataHasDefaultValues() = runTest {
        viewModel = RoundViewModel(
            gamePin,
            FakePitkiotRepository(FakeRepositoryState.Success),
            state,
            UnconfinedTestDispatcher()
        )
        withContext(Dispatchers.Default) {
            delay(5000)
        }
        assertThat(viewModel.uiState.value).isNotNull()
        assertThat(viewModel.uiState.value!!.score).isEqualTo(0)
        assertThat(viewModel.uiState.value!!.skipsLeft).isEqualTo(2)
        assertThat(viewModel.uiState.value!!.curWord).isEqualTo("")
        assertThat(viewModel.uiState.value!!.timeLeftToRound).isEqualTo(60000)
        assertThat(viewModel.uiState.value!!.score).isEqualTo(0)
        assertThat(viewModel.uiState.value!!.curTeam).isEqualTo(Team.TEAM_A)
        assertThat(viewModel.uiState.value!!.curPlayer).isAnyOf("Noga", "Omri", "John", "Mike")
        assertThat(viewModel.uiState.value!!.gameEnded).isFalse()
        assertThat(viewModel.uiState.value!!.showTeamsDivisionDialog).isTrue()
        assertThat(viewModel.uiState.value!!.inRound).isFalse()
        assertThat(viewModel.uiState.value!!.errorMessage).isNull()
        assertThat(viewModel.uiState.value!!.allPitkiot).isEqualTo(setOf("apple", "banana", "cherry", "date", "elderberry", "fig", "grape"))
        assertThat(viewModel.uiState.value!!.allPlayers).isEqualTo(listOf("Noga", "Omri", "John", "Mike"))
        assertThat(viewModel.uiState.value!!.usedWords).isEqualTo(mutableSetOf<String>())
        assertThat(viewModel.uiState.value!!.skippedWords).isEqualTo(mutableSetOf<String>())
        assertThat(viewModel.uiState.value!!.teamAScore).isEqualTo(0)
        assertThat(viewModel.uiState.value!!.playerIndexTeamA).isEqualTo(0)
        assertThat(viewModel.uiState.value!!.playersTeamA).hasSize(2)
        assertThat(viewModel.uiState.value!!.teamBScore).isEqualTo(0)
        assertThat(viewModel.uiState.value!!.playerIndexTeamB).isEqualTo(0)
        assertThat(viewModel.uiState.value!!.playersTeamB).hasSize(2)
    }

    @Test
    fun onInitializationFailure_uiStateLiveDataHasDefaultValues() = runTest {
        viewModel = RoundViewModel(
            gamePin,
            FakePitkiotRepository(FakeRepositoryState.Failure),
            state,
            UnconfinedTestDispatcher()
        )
        withContext(Dispatchers.Default) {
            delay(5000)
        }
        assertThat(viewModel.uiState.value!!.errorMessage).isEqualTo("error")
    }

    @Test
    fun onGetPlayersByTeam_uiStateLiveDataHasDefaultValues() = runTest {
        viewModel = RoundViewModel(
            gamePin,
            FakePitkiotRepository(FakeRepositoryState.Success),
            state,
            UnconfinedTestDispatcher()
        )
        withContext(Dispatchers.Default) {
            delay(5000)
        }
        assertThat(viewModel.getPlayersByTeam(Team.TEAM_A)).hasSize(2)
    }

    @Test
    fun onSkipAttemptSuccessful_uiStateLiveDataUpdatedCorrectly() = runTest {
        viewModel = RoundViewModel(
            gamePin,
            FakePitkiotRepository(FakeRepositoryState.Success),
            state,
            UnconfinedTestDispatcher()
        )
        withContext(Dispatchers.Default) {
            delay(5000)
        }
        viewModel.startNewRound()
        val wordBeforeSkip = viewModel.uiState.value!!.curWord
        viewModel.onSkipAttempt()
        assertThat(wordBeforeSkip).isNotEqualTo(viewModel.uiState.value!!.curWord)
        assertThat(viewModel.uiState.value!!.skippedWords.contains(wordBeforeSkip)).isTrue()
    }
}