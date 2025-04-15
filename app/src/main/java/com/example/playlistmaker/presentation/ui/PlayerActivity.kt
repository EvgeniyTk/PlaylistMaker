package com.example.playlistmaker.presentation.ui

import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.Group
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.presentation.ui.SearchActivity.Companion.TRACK
import com.example.playlistmaker.domain.models.Track
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val DELAY = 330L
    }

    private var playerState = STATE_DEFAULT
    private var mediaPlayer = MediaPlayer()

    private lateinit var toolbar: Toolbar
    private lateinit var trackImage: ImageView
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var playButton: ImageButton
    private lateinit var playTime: TextView
    private lateinit var trackDuration: TextView
    private lateinit var collection: TextView
    private lateinit var releaseDate: TextView
    private lateinit var genre: TextView
    private lateinit var country: TextView
    private lateinit var constraintGroup: Group
    private lateinit var url: String
    private var handler: Handler? = null
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_player)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        toolbar = findViewById(R.id.toolbar_player)
        trackImage = findViewById(R.id.track_image)
        trackName = findViewById(R.id.track_name)
        artistName = findViewById(R.id.artist_name)

        trackDuration = findViewById(R.id.track_duration_value)
        collection = findViewById(R.id.collection_value)
        releaseDate = findViewById(R.id.release_date_value)
        genre = findViewById(R.id.genre_value)
        country = findViewById(R.id.country_value)
        constraintGroup = findViewById(R.id.info_group)
        playButton = findViewById(R.id.play_button)
        playTime = findViewById(R.id.time_play)
        handler = Handler(Looper.getMainLooper())


        toolbar.setNavigationOnClickListener {
            finish()
        }

        val track: Track? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(TRACK, Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(TRACK)
        }

        if (track != null) {
            Glide.with(applicationContext)
                .load(track.getCoverArtwork())
                .transform(CenterCrop(), RoundedCorners(16))
                .placeholder(R.drawable.track_placeholder)
                .into(trackImage)

            trackName.text = track.trackName
            artistName.text = track.artistName
            trackDuration.text =
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis).toString()
            if (track.collectionName.isEmpty()) {
                constraintGroup.visibility = View.GONE
            } else {
                collection.text = track.collectionName
            }
            releaseDate.text = track.releaseDate.substring(0, 4)
            genre.text = track.primaryGenreName
            country.text = track.country

            url = track.previewUrl
            preparePlayer()
        }



        playButton.setOnClickListener {
            playbackControl()
        }

    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        handler?.removeCallbacks(updateRunnable)
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playButton.isEnabled = true
            playerState = STATE_PREPARED
            playTime.text = dateFormat.format(0)
        }
        mediaPlayer.setOnCompletionListener {
            playButton.setImageResource(R.drawable.play_button)
            playerState = STATE_PREPARED
            handler?.removeCallbacks(updateRunnable)
            playTime.text = dateFormat.format(0)
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playButton.setImageResource(R.drawable.pause_button)
        playerState = STATE_PLAYING
        startTimer()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playButton.setImageResource(R.drawable.play_button)
        playerState = STATE_PAUSED
        handler?.removeCallbacks(updateRunnable)
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun startTimer(){
        handler?.post(updateRunnable)
    }

    private val updateRunnable =  object : Runnable {
            override fun run() {
                playTime.text = dateFormat.format(mediaPlayer.currentPosition)
                handler?.postDelayed(this, DELAY)

            }
        }
    }

