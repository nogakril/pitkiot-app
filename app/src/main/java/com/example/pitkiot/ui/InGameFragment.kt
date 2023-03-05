package com.example.pitkiot.ui

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import com.example.pitkiot.R
import com.example.pitkiot.ui.dialog.buildExitDialog

class InGameFragment : Fragment(R.layout.fragment_in_game) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            buildExitDialog(requireContext(), requireActivity())
        }
    }
}