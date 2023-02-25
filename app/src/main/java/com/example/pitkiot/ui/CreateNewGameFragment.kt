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
import com.example.pitkiot.data.enums.Role.ADMIN
import com.example.pitkiot.data.models.UiState.Companion.showError
import com.example.pitkiot.viewmodel.CreateNewGameViewModel
import com.example.pitkiot.viewmodel.factory.CreateNewGameViewModelFactory

class CreateNewGameFragment : Fragment(R.layout.fragment_create_new_game) {

    private lateinit var viewModel: CreateNewGameViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(
            /* owner = */ this,
            /* factory = */ CreateNewGameViewModelFactory(
                pitkiotRepositoryFactory = ::PitkiotRepository
            )
        ).get()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adminNameText = view.findViewById<EditText>(R.id.admin_nickname_edit_text)
        val registerAdminBtn = view.findViewById<Button>(R.id.register_admin_btn)

        registerAdminBtn.setOnClickListener {
            viewModel.createGame(adminNameText.text.toString())
        }

        viewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            uiState.errorMessage?.let { uiState.showError(requireContext()) }
            uiState.gamePin?.let {
                val action = CreateNewGameFragmentDirections.actionCreateNewGameFragmentToAdminWaitingRoomFragment(it, ADMIN)
                findNavController().navigate(action)
            }
        }
    }
}