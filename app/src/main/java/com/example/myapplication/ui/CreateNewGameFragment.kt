package com.example.myapplication.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.myapplication.R
import com.example.myapplication.viewmodel.CreateNewGameViewModel

class CreateNewGameFragment : Fragment(R.layout.fragment_create_new_game) {

    private lateinit var viewModel: CreateNewGameViewModel
    private lateinit var adminNameText: EditText

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adminNameText = view.findViewById<EditText>(R.id.admin_nickname_edit_text)
        view.findViewById<Button>(R.id.register_admin_btn).setOnClickListener(::handleRegisterAdmin)
    }

    private fun handleRegisterAdmin(view: View?) {
        val playerName = adminNameText.text.toString()
        Toast.makeText(context, playerName, Toast.LENGTH_SHORT).show()
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