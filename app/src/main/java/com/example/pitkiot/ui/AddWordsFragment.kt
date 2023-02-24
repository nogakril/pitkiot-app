package com.example.pitkiot.ui

import android.os.Bundle
import android.view.View
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.pitkiot.R
import com.example.pitkiot.data.PitkiotApi
import com.example.pitkiot.data.PitkiotRepository
import com.example.pitkiot.data.enums.GameStatus
import com.example.pitkiot.data.enums.Role
import com.example.pitkiot.utils.showError
import com.example.pitkiot.viewmodel.AddWordsViewModel

class AddWordsFragment : Fragment(R.layout.fragment_add_words) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: AddWordsFragmentArgs by navArgs()
        val viewModel = AddWordsViewModel(PitkiotRepository(PitkiotApi.instance), args.gamePin)
        val addWordText = view.findViewById<EditText>(R.id.add_word_edit_text)
        val addWordBtn = view.findViewById<Button>(R.id.add_words_btn)
        val startGameBtn = view.findViewById<Button>(R.id.start_creating_pitkiot_btn)

        if (args.userRole == Role.ADMIN) {
            startGameBtn.visibility = VISIBLE
        }

        addWordBtn.setOnClickListener {
            viewModel.addWords(addWordText.text.toString())
            addWordText.text.clear()
        }

        startGameBtn.setOnClickListener {
            viewModel.setGameStatus(GameStatus.GAME)
        }

        viewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            uiState.errorMessage?.let { showError(requireContext(), it) }
            if (uiState.gameStatus == GameStatus.GAME) {
                if (args.userRole == Role.ADMIN) {
                    val action = AddWordsFragmentDirections.actionAddWordsFragmentToRoundFragment(args.gamePin)
                    findNavController().navigate(action)
                } else {
                    val action = AddWordsFragmentDirections.actionAddWordsFragmentToInGameFragment(args.gamePin)
                    findNavController().navigate(action)
                }
            }
        }
    }
}