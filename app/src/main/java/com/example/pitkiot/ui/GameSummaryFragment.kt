package com.example.pitkiot.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.pitkiot.R
import com.example.pitkiot.data.PitkiotRepositoryImpl
import com.example.pitkiot.data.enums.GameStatus.GAME_ENDED
import com.example.pitkiot.data.enums.Team.TEAM_A
import com.example.pitkiot.data.enums.Team.TEAM_B
import com.example.pitkiot.ui.dialog.buildExitDialog
import com.example.pitkiot.viewmodel.GameSummaryViewModel

class GameSummaryFragment : Fragment(R.layout.fragment_game_summary) {

    private val args: GameSummaryFragmentArgs by navArgs()
    private lateinit var viewModel: GameSummaryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(
            /* owner = */ this,
            /* factory = */ GameSummaryViewModel.Factory(
                pitkiotRepositoryFactory = ::PitkiotRepositoryImpl,
                gamePinFactory = { args.gamePin }
            )
        ).get()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val winnerLoserText = view.findViewById<TextView>(R.id.winner_loser_text)
        val playAgainBtn = view.findViewById<Button>(R.id.play_again_btn)
        val backToMenuBtn = view.findViewById<Button>(R.id.back_to_menu_btn)

        winnerLoserText.text = getString(
            R.string.winner_loser_text,
            args.winner.customName,
            TEAM_A.customName,
            args.teamAScore,
            TEAM_B.customName,
            args.teamBScore
        )

        playAgainBtn.setOnClickListener {
            val action = GameSummaryFragmentDirections.actionGameSummaryFragmentToRoundFragment(args.gamePin)
            findNavController().navigate(action)
        }

        backToMenuBtn.setOnClickListener {
            viewModel.setGameStatus(GAME_ENDED)
            val action = GameSummaryFragmentDirections.actionGameSummaryFragmentToStartMenuFragment()
            findNavController().navigate(action)
        }

        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            buildExitDialog(requireContext(), requireActivity())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.setGameStatus(GAME_ENDED)
    }
}