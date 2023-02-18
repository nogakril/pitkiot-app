package com.example.pitkiot.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.fragment.findNavController
import com.example.pitkiot.R
import com.example.pitkiot.data.PitkiotRepository
import com.example.pitkiot.viewmodel.GameViewModel
import com.example.pitkiot.viewmodel.GameViewModelFactory

class CreateNewGameFragment : Fragment(R.layout.fragment_create_new_game) {

    private lateinit var viewModel: GameViewModel
    private lateinit var adminNameText: EditText

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, GameViewModelFactory(::PitkiotRepository)).get()
        adminNameText = view.findViewById<EditText>(R.id.admin_nickname_edit_text)
        view.findViewById<Button>(R.id.register_admin_btn).setOnClickListener(::handleRegisterAdmin)
    }

    private fun handleRegisterAdmin(view: View?) {
        viewModel.createGame(adminNameText.text.toString())
        navigateToAdminWaitingRoom(view)
    }

    private fun validateNickName() {
        TODO() // Make sure: name valid (not to long, no curses? maybe there a funny API for that), unique name in game, else show error
    }

    private fun navigateToAdminWaitingRoom(view: View?) {
        val action = CreateNewGameFragmentDirections.actionCreateNewGameFragmentToAdminWaitingRoomFragment()
        findNavController().navigate(action)
    }
}