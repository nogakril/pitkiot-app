package com.example.pitkiot.ui

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pitkiot.R
import com.example.pitkiot.data.PitkiotApi
import com.example.pitkiot.data.PitkiotRepository
import com.example.pitkiot.data.enums.Team.TEAM_A
import com.example.pitkiot.data.enums.Team.TEAM_B
import com.example.pitkiot.utils.OnSwipeTouchListener
import com.example.pitkiot.viewmodel.PlayersListViewAdapter
import com.example.pitkiot.viewmodel.RoundViewModel

class RoundFragment : Fragment(R.layout.fragment_round) {

    lateinit var viewModel: RoundViewModel
    lateinit var swipeView: View
    lateinit var countdownText: TextView
    lateinit var wordTextView: TextView
    lateinit var startRoundTitle: TextView
    lateinit var scoreAndSkipsText: TextView
    lateinit var scoreSummaryText: TextView
    lateinit var nextTeamAndPlayerText: TextView
    lateinit var start_round_btn: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: RoundFragmentArgs by navArgs()
        viewModel = RoundViewModel(args.gamePin, PitkiotRepository(PitkiotApi.instance))
        countdownText = view.findViewById(R.id.countdown_text)
        wordTextView = view.findViewById(R.id.word_text_view)
        startRoundTitle = view.findViewById(R.id.start_round_title)
        swipeView = view.findViewById(R.id.swipe_view)
        scoreAndSkipsText = view.findViewById(R.id.score_and_skips_text)
        nextTeamAndPlayerText = view.findViewById(R.id.next_team_and_player_text)
        scoreSummaryText = view.findViewById(R.id.score_summary_text)
        start_round_btn = view.findViewById(R.id.start_round_btn)

        showTeamsDivisionDialog()

        start_round_btn.setOnClickListener {
            viewModel.startNewRound()
            setRoundUiComponentsVisibility(roundStart = true)
        }

        viewModel.uiState.observe(viewLifecycleOwner) {
            scoreAndSkipsText.text = getString(R.string.score_and_skips_placeholder, it.score, it.skipsLeft)
            wordTextView.text = it.curWord
            countdownText.text = it.timeLeftToRound.toString()
            nextTeamAndPlayerText.text = getString(R.string.next_team_and_player_placeholder, it.curPlayer, it.curTeam.customName)
            scoreSummaryText.text = getString(R.string.score_summary_text, TEAM_A.customName, it.teamAScore, TEAM_B.customName, it.teamBScore)
            if (it.timeLeftToRound == 0L) {
                setRoundUiComponentsVisibility(roundStart = false)
            }
            if (it.gameEnded) {
                val winner = viewModel.onGameEndedReturnWinner()
                val action = RoundFragmentDirections.actionRoundFragmentToGameSummaryFragment(it.teamAScore, it.teamBScore, winner, args.gamePin)
                findNavController().navigate(action)
            }
        }

        swipeView.setOnTouchListener(object : OnSwipeTouchListener(requireContext()) {
            override fun onSwipeRight() {
                super.onSwipeRight()
                handleSwipe(ContextCompat.getColor(view.context, R.color.green)) {
                    viewModel.onCorrectGuess()
                }
            }
            override fun onSwipeLeft() {
                super.onSwipeLeft()
                handleSwipe(ContextCompat.getColor(view.context, R.color.red)) {
                    viewModel.onSkipAttempt()
                }
            }
        })
    }

    private fun handleSwipe(color: Int, action: () -> Unit) {
        action.invoke()
        colorScreenAfterSwipe(color)
    }

    private fun colorScreenAfterSwipe(color: Int) {
        swipeView.setBackgroundColor(color)
        Handler(Looper.getMainLooper()).postDelayed({
            swipeView.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            )
        }, 500)
    }

    private fun setRoundUiComponentsVisibility(roundStart: Boolean) {
        // Round
        countdownText.visibility = if (roundStart) VISIBLE else INVISIBLE
        swipeView.visibility = if (roundStart) VISIBLE else INVISIBLE
        scoreAndSkipsText.visibility = if (roundStart) VISIBLE else INVISIBLE
        wordTextView.visibility = if (roundStart) VISIBLE else INVISIBLE

        // Ready to play?
        nextTeamAndPlayerText.visibility = if (roundStart) INVISIBLE else VISIBLE
        startRoundTitle.visibility = if (roundStart) INVISIBLE else VISIBLE
        start_round_btn.visibility = if (roundStart) INVISIBLE else VISIBLE
        scoreSummaryText.visibility = if (roundStart) INVISIBLE else VISIBLE
    }

    private fun showTeamsDivisionDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.teams_dialog_layout)

        val playersTeamAListRecyclerView: RecyclerView = dialog.findViewById(R.id.team_a_players_view)
        val playersTeamAListViewAdapter = PlayersListViewAdapter(viewModel.getPlayersByTeam(TEAM_A))
        playersTeamAListRecyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
        playersTeamAListRecyclerView.adapter = playersTeamAListViewAdapter

        val playersTeamBListRecyclerView: RecyclerView = dialog.findViewById(R.id.team_b_players_view)
        val playersTeamBListViewAdapter = PlayersListViewAdapter(viewModel.getPlayersByTeam(TEAM_B))
        playersTeamBListRecyclerView.layoutManager = GridLayoutManager(requireContext(), 1)
        playersTeamBListRecyclerView.adapter = playersTeamBListViewAdapter

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window?.attributes)
        lp.width = (resources.displayMetrics.widthPixels * 0.9).toInt()
        lp.height = (resources.displayMetrics.heightPixels * 0.8).toInt()

        dialog.window?.setLayout(lp.width, lp.height)

        dialog.show()
    }
}