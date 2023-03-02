//package com.example.pitkiot.viewmodel
//
//import android.arch.core.executor.testing.InstantTaskExecutorRule
//import com.example.pitkiot.data.PitkiotRepository
//import com.example.pitkiot.data.models.GameCreationResponse
//import com.google.common.truth.Truth.assertThat
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.test.runTest
//import org.junit.Rule
//import org.junit.Test
//import org.junit.runner.RunWith
///* ktlint-disable */
//import org.mockito.Mockito.*
//import org.robolectric.RobolectricTestRunner
///* ktlint-enable */
//
//@RunWith(RobolectricTestRunner::class)
//@OptIn(ExperimentalCoroutinesApi::class)
//class CreateNewGameViewModelTest {
//    @get:Rule
//    val instantTaskExecutorRule = InstantTaskExecutorRule()
//
//    private lateinit var viewModel: CreateNewGameViewModel
//    private lateinit var mockRepository: PitkiotRepository
//
////    @Before
////    fun setUp() {
////        val mockRepository = mock(PitkiotRepository::class.java)
////        viewModel = CreateNewGameViewModel(mockRepository)
////    }
//
//    @Test
//    fun `createGame with valid nickname updates uiState gamePin`() = runTest {
//        val mockRepository = mock(PitkiotRepository::class.java)
//        viewModel = CreateNewGameViewModel(mockRepository)
//        val adminName = "John"
//        val gameId = "1234"
//        val gameCreationResponse = GameCreationResponse(gameId)
//        `when`(mockRepository.createGame(adminName)).thenReturn(Result.success(gameCreationResponse))
////
//        viewModel.createGame(adminName)
////
//        verify(mockRepository).createGame(adminName)
//
//        assertThat(viewModel.uiState.value?.gamePin).isEqualTo(gameId)
//    }
//
////    @Test
////    fun `createGame with empty nickname updates uiState errorMessage`() = runTest {
////        val adminName = ""
////
////        viewModel.createGame(adminName)
////
////        verifyZeroInteractions(mockRepository)
////
////        assertThat(viewModel.uiState.value?.errorMessage).isEqualTo("You must choose a nickname to create a game")
////    }
////
////    @Test
////    fun `createGame with error updates uiState errorMessage`() = runTest {
////        val adminName = "John"
////        val exception = Exception("Error creating a new game")
////        `when`(mockRepository.createGame(adminName)).thenReturn(Result.failure(exception))
////
////        viewModel.createGame(adminName)
////
////        verify(mockRepository).createGame(adminName)
////
////        assertThat(viewModel.uiState.value?.errorMessage).isEqualTo("Error creating a new game")
////    }
//}