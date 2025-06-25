package com.example.playlistmaker.player.data

import android.media.MediaPlayer
import com.example.playlistmaker.player.domain.PlayerRepository
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerRepositoryImpl(private val mediaPlayer: MediaPlayer) : PlayerRepository {

    private var prepared = false
    private val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())

    override fun setDataSource(url: String) {
        mediaPlayer.reset()
        mediaPlayer.setDataSource(url)
    }

    override fun prepare(onPrepared: () -> Unit) {
        mediaPlayer.setOnPreparedListener {
            prepared = true
            onPrepared()
        }
        mediaPlayer.prepareAsync()
    }

    override fun setOnCompletionListener(onComplete: () -> Unit) {
        mediaPlayer.setOnCompletionListener {
            onComplete()
        }
    }

    override fun start() {
        mediaPlayer.start()
    }

    override fun pause() {
        mediaPlayer.pause()
    }

    override fun release() {
        mediaPlayer.stop()
        mediaPlayer.release()
    }



    override fun isPlaying(): Boolean = mediaPlayer.isPlaying

    override fun isPrepared(): Boolean = prepared

    override fun getCurrentTimeFormatted(): String =
        dateFormat.format(mediaPlayer.currentPosition)
}