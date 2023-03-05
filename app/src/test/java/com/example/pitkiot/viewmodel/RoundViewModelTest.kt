package com.example.pitkiot.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.example.pitkiot.FakePitkiotRepository
import com.example.pitkiot.FakeRepositoryState
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
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

    @Test
    fun simple() = runTest {
        val deferred = async {
            viewModel = RoundViewModel(
                gamePin,
                FakePitkiotRepository(FakeRepositoryState.NoInternet),
                SavedStateHandle(),
                UnconfinedTestDispatcher()
            )
        }
        deferred.await()
        assertThat(viewModel.uiState.value).isNotNull()
    }
}