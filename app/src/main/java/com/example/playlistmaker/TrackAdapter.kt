package com.example.playlistmaker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class TrackAdapter(
    private val tracks: List<Track>
) : RecyclerView.Adapter<TrackAdapter.TrackViewHolder> () {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_view, parent, false)
        return TrackViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount() = tracks.size

    class TrackViewHolder(item: View): RecyclerView.ViewHolder(item) {
        private val trackName: TextView// Название композиции
        private val artistName: TextView // Имя исполнителя
        private val trackTime: TextView // Продолжительность трека
        private val trackImage: ImageView // Ссылка на изображение обложки
        init {
            trackName = itemView.findViewById(R.id.track_name)
            artistName = itemView.findViewById(R.id.artist_name)
            trackTime = itemView.findViewById(R.id.track_time)
            trackImage = itemView.findViewById(R.id.track_image)
        }

        fun bind(model: Track){
            trackName.text = model.trackName
            artistName.text = model.artistName
            trackTime.text = model.trackTime
            Glide.with(itemView).load(model.artworkUrl100).placeholder(R.drawable.track_placeholder).into(trackImage)
        }
    }
}
