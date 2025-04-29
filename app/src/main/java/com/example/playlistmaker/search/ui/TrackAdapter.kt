package com.example.playlistmaker.search.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.databinding.TrackViewBinding
import com.example.playlistmaker.search.domain.models.Track

class TrackAdapter(private val clickListener: TrackClickListener) : RecyclerView.Adapter<TrackViewHolder> () {

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
    }

    override fun getItemCount() = trackList.size

    fun interface TrackClickListener {
        fun onTrackClick(track: Track)
    }

}
