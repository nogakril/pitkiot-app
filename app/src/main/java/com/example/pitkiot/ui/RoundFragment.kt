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
import androidx.navigation.fragment.findNavController
import com.example.pitkiot.R
import com.example.pitkiot.data.PitkiotRepository
import com.example.pitkiot.utils.OnSwipeTouchListener
import com.example.pitkiot.viewmodel.RoundViewModel
import com.example.pitkiot.viewmodel.RoundViewModelFactory

class RoundFragment : Fragment(R.layout.fragment_round) {

    private lateinit var countdownText: TextView
    private lateinit var wordTextView: TextView
    private lateinit var swipeView: View
    private lateinit var scoreText: TextView
    private lateinit var skipsText: TextView
    private lateinit var viewModel: RoundViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, RoundViewModelFactory(::PitkiotRepository)).get()
        countdownText = view.findViewById(R.id.countdown_text)
        wordTextView = view.findViewById(R.id.word_text_view)
        swipeView = view.findViewById(R.id.swipe_view)
        scoreText = view.findViewById(R.id.score_text)
        skipsText = view.findViewById(R.id.skips_text)

        viewModel.roundInfoLiveData.observe(viewLifecycleOwner) {
            if (it.timeLeftToRound == 0L) {
                navigateToGameLobby(view)
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

        viewModel.startNewRound()
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

    private fun navigateToGameLobby(view: View?) {
        val action = RoundFragmentDirections.actionRoundFragmentToGameLobbyFragment2()
        findNavController().navigate(action)
    }
}