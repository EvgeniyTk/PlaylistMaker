package com.example.playlistmaker.player.ui

import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistInPlayerViewBinding
import com.example.playlistmaker.library.domain.model.Playlist
import java.io.File

class PlaylistInPlayerViewHolder(
    private val binding: PlaylistInPlayerViewBinding
): RecyclerView.ViewHolder(binding.root) {
    fun bind(model: Playlist) {
        val imagePath = model.imagePath
        if (!imagePath.isNullOrEmpty()) {
            val imageFile = File(imagePath)
            if (imageFile.exists()) {
                binding.playlistInPlayerIv.setImageURI(Uri.fromFile(imageFile))
            } else {
                binding.playlistInPlayerIv.setImageResource(R.drawable.track_placeholder)
            }
        } else {
            binding.playlistInPlayerIv.setImageResource(R.drawable.track_placeholder)
        }
        binding.playlistInPlayerNameTv.text = model.playlistName
        binding.playlistInPlayerCountTv.text = getWordForm(model.tracksCount)
    }

    private fun getWordForm(count: Int): String{
        return binding.root.context.resources.getQuantityString(R.plurals.track_count, count, count)
    }
}