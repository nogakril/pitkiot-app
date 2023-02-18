package com.example.pitkiot.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.example.pitkiot.R
import com.example.pitkiot.utils.OnSwipeTouchListener
import com.example.pitkiot.viewmodel.GameViewModel
import com.example.pitkiot.viewmodel.GameViewModelFactory

class RoundFragment : Fragment(R.layout.fragment_round) {

    private lateinit var countdownText: TextView
    private lateinit var wordTextView: TextView
    private lateinit var swipeView: View
    private lateinit var scoreText: TextView
    private lateinit var skipsText: TextView
    private lateinit var viewModel: GameViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, GameViewModelFactory()).get()
        countdownText = view.findViewById(R.id.countdown_text)
        wordTextView = view.findViewById(R.id.word_text_view)
        swipeView = view.findViewById(R.id.swipe_view)
        scoreText = view.findViewById(R.id.score_text)
        skipsText = view.findViewById(R.id.skips_text)

        viewModel.roundInfoLiveData.observe(viewLifecycleOwner) {
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
        }, 1000)
    }
}