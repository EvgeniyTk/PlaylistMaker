package com.example.playlistmaker

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class TrackAdapter(private val clickListener: TrackClickListener) : RecyclerView.Adapter<TrackViewHolder> () {

    private var trackList: MutableList<Track> = mutableListOf()
    fun updateData(newTrackList: MutableList<Track>){
        trackList = newTrackList
        notifyDataSetChanged()

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        return TrackViewHolder(parent)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(trackList[position])
        holder.itemView.setOnClickListener {
            clickListener.onTrackClick(trackList[position])
            notifyDataSetChanged()
        }
    }

    override fun getItemCount() = trackList.size

    fun interface TrackClickListener {
        fun onTrackClick(track: Track)
    }

}
