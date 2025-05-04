package com.example.playlistmaker.player.domain.impl

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.player.domain.PlayerRepository
import com.example.playlistmaker.player.model.PlayerState

class PlayerInteractorImpl(
    private val repository: PlayerRepository
) : PlayerInteractor {

    private val _playerState = MutableLiveData<PlayerState>()
    private val handler = Handler(Looper.getMainLooper())
    private val updateRunnable = object : Runnable {
        override fun run() {
            _playerState.value = PlayerState.TimeUpdated(repository.getCurrentTimeFormatted())
            handler.postDelayed(this, 330L)
        }
    }

    override fun getPlayerState(): LiveData<PlayerState> = _playerState

    override fun setUrl(url: String) {
        repository.setDataSource(url)
        repository.prepare {
            _playerState.value = PlayerState.Prepared("00:00")
        }
        repository.setOnCompletionListener {
            _playerState.value = PlayerState.Prepared("00:00")
            _playerState.value = PlayerState.Paused
            handler.removeCallbacks(updateRunnable)
        }
    }

    override fun playbackControl() {
        when {
            repository.isPlaying() -> pause()
            repository.isPrepared() -> play()
        }
    }

    private fun play() {
        repository.start()
        _playerState.value = PlayerState.Playing
        handler.post(updateRunnable)
    }

    private fun pause() {
        repository.pause()
        _playerState.value = PlayerState.Paused
        handler.removeCallbacks(updateRunnable)
    }

    override fun pauseIfNeeded() {
        if (repository.isPlaying()) {
            pause()
        }
    }

    override fun release() {
        repository.release()
        handler.removeCallbacks(updateRunnable)
    }
}