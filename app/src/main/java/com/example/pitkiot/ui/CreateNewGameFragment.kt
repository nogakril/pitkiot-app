package com.example.pitkiot.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.pitkiot.R
import com.example.pitkiot.data.PitkiotApi
import com.example.pitkiot.data.PitkiotRepository
import com.example.pitkiot.data.enums.Role.ADMIN
import com.example.pitkiot.utils.showError
import com.example.pitkiot.viewmodel.CreateNewGameViewModel

class CreateNewGameFragment : Fragment(R.layout.fragment_create_new_game) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel = CreateNewGameViewModel(PitkiotRepository(PitkiotApi.instance))
        val adminNameText = view.findViewById<EditText>(R.id.admin_nickname_edit_text)
        val registerAdminBtn = view.findViewById<Button>(R.id.register_admin_btn)

        registerAdminBtn.setOnClickListener {
            viewModel.createGame(adminNameText.text.toString())
        }

        viewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            uiState.errorMessage?.let {
                showError(requireContext(), it)
                viewModel.resetError()
            }
            uiState.gamePin?.let {
                val action = CreateNewGameFragmentDirections.actionCreateNewGameFragmentToAdminWaitingRoomFragment(it, ADMIN)
                findNavController().navigate(action)
            }
        }
    }
}