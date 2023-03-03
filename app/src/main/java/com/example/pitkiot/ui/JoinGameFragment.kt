package com.example.pitkiot.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.fragment.findNavController
import com.example.pitkiot.R
import com.example.pitkiot.data.PitkiotRepositoryImpl
import com.example.pitkiot.data.enums.Role.PLAYER
import com.example.pitkiot.data.models.UiState.Companion.showError
import com.example.pitkiot.ui.dialog.buildExitDialog
import com.example.pitkiot.viewmodel.JoinGameViewModel

class JoinGameFragment : Fragment(R.layout.fragment_join_game) {

    private lateinit var viewModel: JoinGameViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(
            /* owner = */ this,
            /* factory = */ JoinGameViewModel.Factory(
                pitkiotRepositoryFactory = ::PitkiotRepositoryImpl
            )
        ).get()

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            buildExitDialog(requireContext(), requireActivity())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val playerNameText = view.findViewById<EditText>(R.id.player_nickname_edit_text)
        val gamePinText = view.findViewById<EditText>(R.id.game_pin_edit_text)
        val registerPlayerBtn = view.findViewById<Button>(R.id.register_player_btn)

        registerPlayerBtn.setOnClickListener {
            viewModel.joinGame(gamePinText.text.toString(), playerNameText.text.toString())
        }

        viewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            uiState.errorMessage?.let { uiState.showError(requireContext()) }
            uiState.gamePin?.let {
                val action = JoinGameFragmentDirections.actionJoinGameFragmentToAdminWaitingRoomFragment2(it, PLAYER)
                findNavController().navigate(action)
            }
        }
    }
}