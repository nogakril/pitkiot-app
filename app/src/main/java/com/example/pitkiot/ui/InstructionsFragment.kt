package com.example.pitkiot.ui

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.pitkiot.R
import java.io.BufferedReader
import java.io.InputStreamReader

class InstructionsFragment : Fragment(R.layout.fragment_instructions) {

    private lateinit var instructions: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val inputStream = resources.openRawResource(R.raw.instructions)
        val reader = BufferedReader(InputStreamReader(inputStream))
        val builder = StringBuilder()
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            if (line!!.endsWith("---")) {
                builder.append("\n\n").append(line).append("\n\n")
            } else {
                builder.append(line)
            }
        }
        reader.close()
        instructions = builder.toString()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val instructionsText = view.findViewById<TextView>(R.id.instructions_text)
        instructionsText.text = instructions
    }
}