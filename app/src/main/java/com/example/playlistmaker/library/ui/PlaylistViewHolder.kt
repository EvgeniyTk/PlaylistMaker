package com.example.playlistmaker.library.ui

import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.PlaylistViewBinding
import com.example.playlistmaker.library.domain.model.Playlist
import java.io.File

class PlaylistViewHolder(
    private val binding: PlaylistViewBinding
): RecyclerView.ViewHolder(binding.root) {
    fun bind(model: Playlist){
        val imagePath = model.imagePath
        if (!imagePath.isNullOrEmpty()) {
            val imageFile = File(imagePath)
            if (imageFile.exists()) {
                binding.playlistIv.setImageURI(Uri.fromFile(imageFile))
            } else {
                binding.playlistIv.setImageResource(R.drawable.track_placeholder)
            }
        } else {
            binding.playlistIv.setImageResource(R.drawable.track_placeholder)
        }
        binding.playlistNameTv.text = model.playlistName
        binding.playlistCounterTv.text = getWordForm(model.tracksCount)
    }

    private fun getWordForm(count: Int): String{
        val n = count % 100
        return if (n in 11..19) {
            "$count треков"
        } else {
            when (n % 10) {
                1 -> "$count трек"
                2, 3, 4 -> "$count трека"
                else -> "$count треков"
            }
        }
    }
}