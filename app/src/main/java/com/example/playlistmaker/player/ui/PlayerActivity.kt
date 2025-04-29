package com.example.playlistmaker.player.ui

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityPlayerBinding
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.player.view_model.PlayerViewModel
import com.example.playlistmaker.search.ui.SearchActivity.Companion.TRACK
import java.text.SimpleDateFormat
import java.util.Locale


class PlayerActivity : AppCompatActivity() {

    private lateinit var viewModel: PlayerViewModel

    private lateinit var binding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel = ViewModelProvider(this)[PlayerViewModel::class.java]

        viewModel.playTime.observe(this) { time ->
            binding.timePlay.text = time
        }

        viewModel.isPlaying.observe(this) { isPlaying ->
            if (isPlaying) {
                binding.playButton.setImageResource(R.drawable.pause_button)
            } else {
                binding.playButton.setImageResource(R.drawable.play_button)
            }
        }

        viewModel.isPrepared.observe(this) { prepared ->
            binding.playButton.isEnabled = prepared
        }

        binding.toolbarPlayer.setNavigationOnClickListener {
            finish()
        }

        val track: Track? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(TRACK, Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(TRACK)
        }

        if (track != null) {
            bindTrack(track)
            viewModel.setUrl(track.previewUrl)
        }

        binding.playButton.setOnClickListener {
            viewModel.playbackControl()
        }
    }

    private fun bindTrack(track: Track) {
        Glide.with(applicationContext)
            .load(track.getCoverArtwork())
            .transform(CenterCrop(), RoundedCorners(16))
            .placeholder(R.drawable.track_placeholder)
            .into(binding.trackImage)

        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.trackDurationValue.text = SimpleDateFormat("mm:ss", Locale.getDefault())
            .format(track.trackTimeMillis)

        if (track.collectionName.isEmpty()) {
            binding.infoGroup.visibility = View.GONE
        } else {
            binding.collectionValue.text = track.collectionName
        }

        binding.releaseDateValue.text = track.releaseDate.substring(0, 4)
        binding.genreValue.text = track.primaryGenreName
        binding.countryValue.text = track.country
    }

    override fun onPause() {
        super.onPause()
        viewModel.pauseIfNeeded()
    }
}
