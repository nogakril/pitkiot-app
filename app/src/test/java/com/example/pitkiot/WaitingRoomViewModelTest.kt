package com.example.pitkiot

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.pitkiot.data.PitkiotRepository
import com.example.pitkiot.data.models.PlayersGetterResponse
import com.example.pitkiot.viewmodel.WaitingRoomViewModel
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
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
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.given
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class WaitingRoomViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mockitoRule: MockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var mockRepository: PitkiotRepository

    private lateinit var viewModel: WaitingRoomViewModel

    private val gamePin = "1234"

    @Before
    fun setUp() {
        viewModel = WaitingRoomViewModel(mockRepository, gamePin, UnconfinedTestDispatcher())
    }

    @Test
    fun `getPlayers with success response updates players`() = runTest {
        val fakeResult = PlayersGetterResponse(listOf("player1", "player2"))
        whenever(mockRepository.getPlayers("1")).thenReturn(
            Result.success(fakeResult))
        viewModel.getPlayers()

        advanceTimeBy(1000) // wait for getPlayers to update uiState
        verify(mockRepository).getPlayers("1")
//        assertThat(viewModel.uiState.value?.players).isEqualTo(fakeResult.players)
    }

}
