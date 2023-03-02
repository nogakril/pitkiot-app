package com.example.pitkiot.ui

import android.os.Bundle
import android.view.View
import android.view.View.VISIBLE
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.pitkiot.R
import com.example.pitkiot.data.PitkiotRepositoryImpl
import com.example.pitkiot.data.enums.GameStatus.IN_GAME
import com.example.pitkiot.data.enums.Role.ADMIN
import com.example.pitkiot.data.models.UiState.Companion.showError
import com.example.pitkiot.viewmodel.AddWordsViewModel

class AddWordsFragment : Fragment(R.layout.fragment_add_words) {

    private val args: AddWordsFragmentArgs by navArgs()
    private lateinit var viewModel: AddWordsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(
            /* owner = */ this,
            /* factory = */ AddWordsViewModel.Factory(
                pitkiotRepositoryFactory = ::PitkiotRepositoryImpl,
                gamePinFactory = { args.gamePin }
            )
        ).get()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val addWordText = view.findViewById<EditText>(R.id.add_word_edit_text)
        val addWordBtn = view.findViewById<Button>(R.id.add_words_btn)
        val startGameBtn = view.findViewById<Button>(R.id.start_game_btn)

        if (args.userRole == ADMIN) {
            startGameBtn.visibility = VISIBLE
        }

        addWordBtn.setOnClickListener {
            viewModel.addWords(addWordText.text.toString())
            addWordText.text.clear()
        }

        startGameBtn.setOnClickListener {
            viewModel.setGameStatus(IN_GAME)
        }

        viewModel.checkGameStatus()

        viewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            uiState.errorMessage?.let { uiState.showError(requireContext()) }
            if (uiState.gameStatus == IN_GAME) {
                if (args.userRole == ADMIN) {
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