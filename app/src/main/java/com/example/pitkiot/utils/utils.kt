package com.example.pitkiot.utils

import android.app.AlertDialog
import android.content.Context
import androidx.fragment.app.FragmentActivity


fun buildExitDialog (context: Context,  fragmentActivity: FragmentActivity) {
    AlertDialog.Builder(context)
        .setMessage("Are you sure you want to exit?")
        .setPositiveButton("Yes") { _, _ ->
            // If the user clicks "Yes", exit the app
            fragmentActivity.finish()
        }
        .setNegativeButton("No", null)
        .show()
}