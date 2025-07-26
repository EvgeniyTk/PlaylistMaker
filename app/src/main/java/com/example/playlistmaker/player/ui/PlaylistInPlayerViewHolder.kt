package com.example.playlistmaker.player.ui


import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistInPlayerViewBinding
import com.example.playlistmaker.library.domain.model.Playlist
import java.io.File

class PlaylistInPlayerViewHolder(
    private val binding: PlaylistInPlayerViewBinding
): RecyclerView.ViewHolder(binding.root) {
    fun bind(model: Playlist) {
        val imageFile = model.imagePath?.let { File(it) }
        Glide.with(binding.playlistInPlayerIv.context)
            .load(if (imageFile?.exists() == true) imageFile else null)
            .placeholder(R.drawable.track_placeholder)
            .error(R.drawable.track_placeholder)
            .into(binding.playlistInPlayerIv)

        binding.playlistInPlayerNameTv.text = model.playlistName
        binding.playlistInPlayerCountTv.text = getWordForm(model.tracksCount)
    }

    private fun getWordForm(count: Int): String{
        return binding.root.context.resources.getQuantityString(R.plurals.track_count, count, count)
    }
}