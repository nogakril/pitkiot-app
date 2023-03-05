package com.example.pitkiot.data.models

import android.content.Context
import android.widget.Toast
import java.io.Serializable

interface UiState : Serializable {
    var errorMessage: String?

    companion object {
        fun UiState.showError(context: Context) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
            errorMessage = null
        }
    }
}