package com.example.pitkiot.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.fragment.findNavController
import com.example.pitkiot.R
import com.example.pitkiot.data.PitkiotRepository
import com.example.pitkiot.data.enums.GameStatus
import com.example.pitkiot.viewmodel.GameViewModel
import com.example.pitkiot.viewmodel.GameViewModelFactory

class AddWordsFragment : Fragment(R.layout.fragment_add_words) {
    private lateinit var viewModel: GameViewModel
    private lateinit var addWordText: TextView
    private lateinit var countdownText: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, GameViewModelFactory(::PitkiotRepository)).get()
        addWordText = view.findViewById(R.id.add_word_edit_text)
        countdownText = view.findViewById(R.id.add_words_countdown_text)
        view.findViewById<Button>(R.id.add_words_btn).setOnClickListener(::addWordHandler)
        viewModel.gameInfoLiveData.observe(viewLifecycleOwner) {
            if (it.gameStatus == GameStatus.GAME) {
                navigateToGame(view)
            }
            val secondsLeft = (it.timeLeftToAddWords % 60)
            val minutesLeft = (it.timeLeftToAddWords / 60)
            countdownText.text = getString(R.string.countdown_placeholder, minutesLeft, secondsLeft)
        }
        viewModel.addWordsTimer.start()
    }

    private fun addWordHandler(view: View?) {
        viewModel.addWordToGame(addWordText.text.toString())
        addWordText.text = ""
    }
    private fun navigateToGame(view: View?) {
        val action = AddWordsFragmentDirections.actionAddWordsFragmentToGameLobbyFragment()
        findNavController().navigate(action)
    }
}