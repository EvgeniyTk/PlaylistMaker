package com.example.playlistmaker.presentation.ui

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.domain.models.Track

class TrackAdapter(private val clickListener: TrackClickListener) : RecyclerView.Adapter<TrackViewHolder> () {

    private var trackList: MutableList<Track> = mutableListOf()
    fun updateData(newTrackList: List<Track>){
        trackList = newTrackList.toMutableList()
        notifyDataSetChanged()

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        return TrackViewHolder(parent)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(trackList[position])
        holder.itemView.setOnClickListener {
            clickListener.onTrackClick(trackList[position])
            notifyItemChanged(position)
        }
    }

    override fun getItemCount() = trackList.size

    fun interface TrackClickListener {
        fun onTrackClick(track: Track)
    }

}
