package com.example.pitkiot.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.fragment.findNavController
import com.example.pitkiot.R
import com.example.pitkiot.data.PitkiotRepository
import com.example.pitkiot.viewmodel.GameViewModel
import com.example.pitkiot.viewmodel.GameViewModelFactory

class GameLobbyFragment : Fragment(R.layout.fragment_game_lobby) {

    private lateinit var viewModel: GameViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, GameViewModelFactory(::PitkiotRepository)).get()
        view.findViewById<Button>(R.id.start_round_btn).setOnClickListener(::navigateToRound)
    }

    private fun navigateToRound(view: View?) {
        val action = GameLobbyFragmentDirections.actionGameLobbyFragmentToRoundFragment()
        findNavController().navigate(action)
    }
}