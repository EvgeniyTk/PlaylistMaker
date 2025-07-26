package com.example.playlistmaker.player.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.PlaylistInPlayerViewBinding
import com.example.playlistmaker.library.domain.model.Playlist

class PlaylistInPlayerAdapter(private val clickListener: PlaylistClickListener): RecyclerView.Adapter<PlaylistInPlayerViewHolder>() {
    private var list:MutableList<Playlist> = mutableListOf()

    fun updateData(newList: List<Playlist>) {
        list = newList.toMutableList()
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistInPlayerViewHolder {
        val binding = PlaylistInPlayerViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaylistInPlayerViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: PlaylistInPlayerViewHolder, position: Int) {
        holder.bind(list[position])
        holder.itemView.setOnClickListener{
            clickListener.onPlaylistClick(list[position])
        }
    }

    fun interface PlaylistClickListener {
        fun onPlaylistClick(playlist: Playlist)
    }
}