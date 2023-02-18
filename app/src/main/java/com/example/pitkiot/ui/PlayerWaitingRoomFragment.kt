package com.example.pitkiot.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.fragment.findNavController
import com.example.pitkiot.R
import com.example.pitkiot.data.PitkiotRepository
import com.example.pitkiot.data.enums.GameStatus
import com.example.pitkiot.viewmodel.GameViewModel
import com.example.pitkiot.viewmodel.GameViewModelFactory

class PlayerWaitingRoomFragment : Fragment(R.layout.fragment_player_waiting_room) {
    private lateinit var viewModel: GameViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, GameViewModelFactory(::PitkiotRepository)).get()
        viewModel.gameInfoLiveData.observe(viewLifecycleOwner) {
            if (it.gameStatus == GameStatus.PITKIOT_CREATION) {
                navigateToAddWords(view) // This happens when admin clicks that the game has started
            }
        }
        viewModel.setGameStatus(GameStatus.PITKIOT_CREATION) // TODO only for testing, delete!!
    }
    private fun navigateToAddWords(view: View?) {
        val action = PlayerWaitingRoomFragmentDirections.actionPlayerWaitingRoomFragmentToAddWordsFragment()
        findNavController().navigate(action)
    }
}