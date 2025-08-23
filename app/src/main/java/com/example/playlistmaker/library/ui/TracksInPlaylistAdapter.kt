package com.example.playlistmaker.library.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.TrackViewBinding
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.TrackViewHolder

class TracksInPlaylistAdapter(
    private val clickListener: TrackClickListener,
    private val longClickListener: LongTrackClickListener
): RecyclerView.Adapter<TrackViewHolder>() {

    private var trackList: MutableList<Track> = mutableListOf()
    fun updateData(newTrackList: List<Track>){
        trackList = newTrackList.toMutableList()
        notifyDataSetChanged()

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = TrackViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(trackList[position])
        holder.itemView.setOnClickListener {
            clickListener.onTrackClick(trackList[position])
        }
        holder.itemView.setOnLongClickListener {
            longClickListener.onTrackLongClick(trackList[position])
            true
        }
    }

    override fun getItemCount() = trackList.size

    fun interface TrackClickListener {
        fun onTrackClick(track: Track)
    }

    fun interface LongTrackClickListener {
        fun onTrackLongClick(track: Track)
    }
}