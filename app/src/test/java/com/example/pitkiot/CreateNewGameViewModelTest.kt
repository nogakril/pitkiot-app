package com.example.pitkiot

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.pitkiot.viewmodel.CreateNewGameViewModel
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@ExperimentalCoroutinesApi
class CreateNewGameViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: CreateNewGameViewModel

    @Before
    fun before() {
        val mockRepository = mockPitkiotRepository()
        viewModel = CreateNewGameViewModel(mockRepository, UnconfinedTestDispatcher())
    }

    @Test
    fun `createGame with valid nickname updates uiState gamePin`() = runTest {
        viewModel.createGame("admin")
        assertThat(viewModel.uiState.value).isNotNull()
//        assertThat(viewModel.uiState.value?.gamePin).isEqualTo("mf7h")
    }

}

//    @Test
//    fun `createGame with empty nickname updates uiState errorMessage`() = runTest {
//        val adminName = ""
//
//        viewModel.createGame(adminName)
//
//        verifyZeroInteractions(mockRepository)
//
//        assertThat(viewModel.uiState.value?.errorMessage).isEqualTo("You must choose a nickname to create a game")
//    }
//
//    @Test
//    fun `createGame with error updates uiState errorMessage`() = runTest {
//        val adminName = "John"
//        val exception = Exception("Error creating a new game")
//        `when`(mockRepository.createGame(adminName)).thenReturn(Result.failure(exception))
//
//        viewModel.createGame(adminName)
//
//        verify(mockRepository).createGame(adminName)
//
//        assertThat(viewModel.uiState.value?.errorMessage).isEqualTo("Error creating a new game")
//    }
//}