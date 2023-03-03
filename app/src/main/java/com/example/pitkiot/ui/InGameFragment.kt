package com.example.pitkiot.ui

import android.os.Bundle
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import com.example.pitkiot.R
import com.example.pitkiot.utils.buildExitDialog

class InGameFragment : Fragment(R.layout.fragment_in_game) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
        buildExitDialog(requireContext(), requireActivity())
    }
}