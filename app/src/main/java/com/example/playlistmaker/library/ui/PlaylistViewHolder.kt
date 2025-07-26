package com.example.playlistmaker.library.ui

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistViewBinding
import com.example.playlistmaker.library.domain.model.Playlist
import java.io.File

class PlaylistViewHolder(
    private val binding: PlaylistViewBinding
): RecyclerView.ViewHolder(binding.root) {
    fun bind(model: Playlist) {
        val imageFile = model.imagePath?.let { File(it) }
        Glide.with(binding.playlistIv.context)
            .load(if (imageFile?.exists() == true) imageFile else null)
            .placeholder(R.drawable.track_placeholder)
            .error(R.drawable.track_placeholder)
            .into(binding.playlistIv)

        binding.playlistNameTv.text = model.playlistName
        binding.playlistCounterTv.text = getWordForm(model.tracksCount)
    }

    private fun getWordForm(count: Int): String{
        return binding.root.context.resources.getQuantityString(R.plurals.track_count, count, count)
    }
}