package com.example.pitkiot.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.WindowManager
import android.widget.Button
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pitkiot.R
import com.example.pitkiot.viewmodel.PlayersListViewAdapter

class TeamsDialog(context: Context, private val playersTeamA: List<String>, private val playersTeamB: List<String>) : Dialog(context) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.teams_dialog_layout)

        val playersTeamAListRecyclerView: RecyclerView = this.findViewById(R.id.team_a_players_view)
        val playersTeamAListViewAdapter = PlayersListViewAdapter(playersTeamA)
        playersTeamAListRecyclerView.layoutManager = GridLayoutManager(context, 1)
        playersTeamAListRecyclerView.adapter = playersTeamAListViewAdapter

        val playersTeamBListRecyclerView: RecyclerView = this.findViewById(R.id.team_b_players_view)
        val playersTeamBListViewAdapter = PlayersListViewAdapter(playersTeamB)
        playersTeamBListRecyclerView.layoutManager = GridLayoutManager(context, 1)
        playersTeamBListRecyclerView.adapter = playersTeamBListViewAdapter

        val closeButton = findViewById<Button>(R.id.dialog_close_btn)
        closeButton.setOnClickListener {
            dismiss()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val window = window ?: return
        val params = window.attributes
        params.width = WindowManager.LayoutParams.MATCH_PARENT
        params.height = WindowManager.LayoutParams.MATCH_PARENT
        window.attributes = params
    }
}