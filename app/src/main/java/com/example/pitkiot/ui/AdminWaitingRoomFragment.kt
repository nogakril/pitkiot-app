package com.example.pitkiot.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.fragment.findNavController
import com.example.pitkiot.R
import com.example.pitkiot.viewmodel.GameViewModel
import com.example.pitkiot.viewmodel.GameViewModelFactory

const val GAME_PIN = "HJK3" // obviously should not be here, get from view model
const val PLAYERS = "Noga \n Omri \n Other"

class AdminWaitingRoomFragment : Fragment(R.layout.fragment_admin_waiting_room) {
    private lateinit var viewModel: GameViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, GameViewModelFactory()).get()
        val gamePinText = view.findViewById<TextView>(R.id.game_pin_title)
        val playersText = view.findViewById<TextView>(R.id.players_list)
        view.findViewById<Button>(R.id.start_game_btn).setOnClickListener(::startGameHandler)
        gamePinText.text = getString(R.string.game_pin_title, GAME_PIN)
        playersText.text = PLAYERS
    }

    private fun startGameHandler(view: View?) {
        viewModel.startGame()
        navigateToGame(view)
    }
    private fun navigateToGame(view: View?) {
        val action = AdminWaitingRoomFragmentDirections.actionAdminWaitingRoomFragmentToAddWordsFragment()
        findNavController().navigate(action)
    }
}