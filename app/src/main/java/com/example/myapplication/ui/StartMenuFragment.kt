package com.example.myapplication.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.viewmodel.StartMenuViewModel

class StartMenuFragment : Fragment(R.layout.fragment_start_menu) {

    private lateinit var viewModel: StartMenuViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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