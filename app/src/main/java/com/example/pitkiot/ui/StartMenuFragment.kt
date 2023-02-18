package com.example.pitkiot.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.fragment.findNavController
import com.example.pitkiot.R
import com.example.pitkiot.data.PitkiotRepository
import com.example.pitkiot.viewmodel.GameViewModel
import com.example.pitkiot.viewmodel.GameViewModelFactory

class StartMenuFragment : Fragment(R.layout.fragment_start_menu) {

    private lateinit var viewModel: GameViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, GameViewModelFactory(::PitkiotRepository)).get()
        view.findViewById<Button>(R.id.create_new_game_btn)?.setOnClickListener(::navigateToCreateNewGame)
        view.findViewById<Button>(R.id.join_game_btn)?.setOnClickListener(::navigateToJoinGame)
    }

    private fun navigateToCreateNewGame(view: View?) {
        val action = StartMenuFragmentDirections.actionStartMenuFragmentToCreateNewGameFragment()
        findNavController().navigate(action)
    }

    private fun navigateToJoinGame(view: View?) {
        val action = StartMenuFragmentDirections.actionStartMenuFragmentToJoinGameFragment()
        findNavController().navigate(action)
    }
}