package com.example.pitkiot.ui

import android.os.Bundle
import android.view.View
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.TextView
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pitkiot.R
import com.example.pitkiot.data.PitkiotRepositoryImpl
import com.example.pitkiot.data.enums.GameStatus.ADDING_WORDS
import com.example.pitkiot.data.enums.Role.ADMIN
import com.example.pitkiot.data.models.UiState.Companion.showError
import com.example.pitkiot.ui.dialog.buildExitDialog
import com.example.pitkiot.viewmodel.PlayersListViewAdapter
import com.example.pitkiot.viewmodel.WaitingRoomViewModel

class WaitingRoomFragment : Fragment(R.layout.fragment_waiting_room) {

    private val args: WaitingRoomFragmentArgs by navArgs()
    private lateinit var viewModel: WaitingRoomViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(
            /* owner = */ this,
            /* factory = */ WaitingRoomViewModel.Factory(
                pitkiotRepositoryFactory = ::PitkiotRepositoryImpl,
                gamePinFactory = { args.gamePin }
            )
        ).get()

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            buildExitDialog(requireContext(), requireActivity())
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val gamePinText = view.findViewById<TextView>(R.id.game_pin_title)
        val startGameBtn = view.findViewById<Button>(R.id.start_creating_pitkiot_btn)
        val playersListRecyclerView: RecyclerView = view.findViewById(R.id.players_list_recycler_view)
        val playersListViewAdapter = PlayersListViewAdapter(emptyList())

        if (args.userRole == ADMIN) {
            startGameBtn.visibility = VISIBLE
        }

        playersListRecyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
        playersListRecyclerView.adapter = playersListViewAdapter
        gamePinText.text = getString(R.string.game_pin_title, args.gamePin)

        viewModel.checkGameStatus()
        viewModel.checkPlayers()

        startGameBtn.setOnClickListener {
            viewModel.setGameStatus(ADDING_WORDS)
        }

        viewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            uiState.errorMessage?.let { uiState.showError(requireContext()) }
            if (uiState.players.isNotEmpty()) {
                playersListViewAdapter.updatePlayersList(uiState.players)
            }
            if (uiState.gameStatus == ADDING_WORDS) {
                val action = WaitingRoomFragmentDirections.actionAdminWaitingRoomFragmentToAddWordsFragment(args.gamePin, args.userRole)
                findNavController().navigate(action)
            }
        }
    }
}