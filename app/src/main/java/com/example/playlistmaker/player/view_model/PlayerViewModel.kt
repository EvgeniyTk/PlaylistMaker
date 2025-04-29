package com.example.playlistmaker.player.view_model

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.Locale


class PlayerViewModel : ViewModel() {

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val DELAY = 330L
    }

    private val mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT
    private val handler = Handler(Looper.getMainLooper())
    private val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())

    private var url: String = ""

    private val _playTime = MutableLiveData<String>()
    val playTime: LiveData<String> get() = _playTime

    private val _isPlaying = MutableLiveData<Boolean>()
    val isPlaying: LiveData<Boolean> get() = _isPlaying

    private val _isPrepared = MutableLiveData<Boolean>()
    val isPrepared: LiveData<Boolean> get() = _isPrepared

    fun setUrl(url: String) {
        this.url = url
        preparePlayer()
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            playerState = STATE_PREPARED
            _isPrepared.value = true
            _playTime.value = dateFormat.format(0)
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
            _isPlaying.value = false
            handler.removeCallbacks(updateRunnable)
            _playTime.value = dateFormat.format(0)
        }
    }

    fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> pausePlayer()
            STATE_PREPARED, STATE_PAUSED -> startPlayer()
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerState = STATE_PLAYING
        _isPlaying.value = true
        startTimer()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
        _isPlaying.value = false
        handler.removeCallbacks(updateRunnable)
    }

    private fun startTimer() {
        handler.post(updateRunnable)
    }

    private val updateRunnable = object : Runnable {
        override fun run() {
            _playTime.value = dateFormat.format(mediaPlayer.currentPosition)
            handler.postDelayed(this, DELAY)
        }
    }

    override fun onCleared() {
        super.onCleared()
        mediaPlayer.release()
        handler.removeCallbacks(updateRunnable)
    }

    fun pauseIfNeeded() {
        if (playerState == STATE_PLAYING) {
            pausePlayer()
        }
    }
}

