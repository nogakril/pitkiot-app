package com.example.pitkiot.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.pitkiot.R
import com.example.pitkiot.data.enums.Team.TEAM_A
import com.example.pitkiot.data.enums.Team.TEAM_B

class GameSummaryFragment : Fragment(R.layout.fragment_game_summary) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: GameSummaryFragmentArgs by navArgs()
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
            val action = GameSummaryFragmentDirections.actionGameSummaryFragmentToStartMenuFragment()
            findNavController().navigate(action)
        }
    }
}