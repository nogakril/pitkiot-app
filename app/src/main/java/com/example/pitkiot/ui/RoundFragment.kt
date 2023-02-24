package com.example.pitkiot.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.pitkiot.R
import com.example.pitkiot.data.PitkiotApi
import com.example.pitkiot.data.PitkiotRepository
import com.example.pitkiot.utils.OnSwipeTouchListener
import com.example.pitkiot.viewmodel.RoundViewModel

class RoundFragment : Fragment(R.layout.fragment_round) {

    lateinit var swipeView: View
    lateinit var countdownText: TextView
    lateinit var wordTextView: TextView
    lateinit var startRoundTitle: TextView
    lateinit var scoreText: TextView
    lateinit var skipsText: TextView
    lateinit var start_round_btn: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: RoundFragmentArgs by navArgs()
        val viewModel = RoundViewModel(args.gamePin, PitkiotRepository(PitkiotApi.instance))
        countdownText = view.findViewById(R.id.countdown_text)
        wordTextView = view.findViewById(R.id.word_text_view)
        startRoundTitle = view.findViewById(R.id.start_round_title)
        swipeView = view.findViewById(R.id.swipe_view)
        scoreText = view.findViewById(R.id.score_text)
        skipsText = view.findViewById(R.id.skips_text)
        start_round_btn = view.findViewById(R.id.start_round_btn)

        start_round_btn.setOnClickListener {
            viewModel.startNewRound()
            setRoundUiComponentsVisibility(roundStart = true)
        }

        viewModel.uiState.observe(viewLifecycleOwner) {
            if (it.timeLeftToRound == 0L) {
                setRoundUiComponentsVisibility(roundStart = false)
            }
            scoreText.text = getString(R.string.score_placeholder, it.score)
            skipsText.text = getString(R.string.skips_placeholder, it.skipsLeft)
            wordTextView.text = it.curWord
            countdownText.text = it.timeLeftToRound.toString()
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
        countdownText.visibility = if (roundStart) VISIBLE else GONE
        scoreText.visibility = if (roundStart) VISIBLE else GONE
        skipsText.visibility = if (roundStart) VISIBLE else GONE
        wordTextView.visibility = if (roundStart) VISIBLE else GONE
        startRoundTitle.visibility = if (roundStart) GONE else VISIBLE
        start_round_btn.visibility = if (roundStart) GONE else VISIBLE
    }
}