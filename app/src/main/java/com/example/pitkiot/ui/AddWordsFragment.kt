package com.example.pitkiot.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.example.pitkiot.R
import com.example.pitkiot.viewmodel.GameViewModel
import com.example.pitkiot.viewmodel.GameViewModelFactory


class AddWordsFragment : Fragment(R.layout.fragment_add_words) {
    private lateinit var viewModel: GameViewModel
    private lateinit var addWordText: TextView
    private lateinit var countdownText: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this, GameViewModelFactory()).get()
        addWordText = view.findViewById(R.id.add_word_edit_text)
        countdownText = view.findViewById(R.id.add_words_countdown_text)
        view.findViewById<Button>(R.id.add_words_btn).setOnClickListener(::addWordHandler)
        viewModel.gameInfoLiveData.observe(viewLifecycleOwner) {
            val secondsLeft = (it.timeLeftToAddWords % 60)
            val minutesLeft = (it.timeLeftToAddWords / 60)
            countdownText.text = getString(R.string.countdown_placeholder, minutesLeft, secondsLeft)
        }
        viewModel.addWordsTimer.start()
    }

    private fun addWordHandler(view: View?) {
        viewModel.addWordToGame(addWordText.text.toString())
        addWordText.text = getString(R.string.add_words_hint).toString()
    }
    private fun navigateToGame(view: View?) {
        TODO()
//        val action = StartMenuFragmentDirections.actionStartMenuFragmentToCreateNewGameFragment()
//        findNavController().navigate(action)
    }
}