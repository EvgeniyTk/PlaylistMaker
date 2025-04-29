package com.example.playlistmaker.search.ui

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.TrackViewBinding
import com.example.playlistmaker.search.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class TrackViewHolder(
    private val binding: TrackViewBinding
): RecyclerView.ViewHolder(binding.root) {

    fun bind(model: Track){
        binding.trackName.text = model.trackName
        binding.artistName.text = model.artistName
        binding.trackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault()).format(model.trackTimeMillis).toString()
        Glide.with(binding.root)
            .load(model.artworkUrl100)
            .transform(CenterCrop(),RoundedCorners(4))
            .placeholder(R.drawable.track_placeholder)
            .into(binding.trackImage)
    }
}