package com.example.pitkiot.utils

import android.content.Context
import android.widget.Toast

fun showError(context: Context, error: String) {
    Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
}