package com.example.pitkiot.viewmodel

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pitkiot.R

class PlayersListViewAdapter(private var players: List<String>) : RecyclerView.Adapter<PlayersListViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.players_list_item, parent, false)
        return ViewHolder(view)
    }

    fun updatePlayersList(newPlayers: List<String>) {
        players = newPlayers
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return players.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(players[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.players_list_item_text)

        fun bind(name: String) {
            nameTextView.text = name
        }
    }
}