package com.example.playlistmaker.player.domain.impl

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.player.domain.PlayerRepository
import com.example.playlistmaker.player.model.PlayerState

class PlayerInteractorImpl(
    private val repository: PlayerRepository
) : PlayerInteractor {

    private val _playerState = MutableLiveData<PlayerState>()

    override fun getPlayerState(): LiveData<PlayerState> = _playerState

    override fun setUrl(url: String) {
        repository.setDataSource(url)
        repository.prepare {
            _playerState.value = PlayerState.Prepared(repository.formatTime(0))
        }
        repository.setOnCompletionListener {
            _playerState.value = PlayerState.Prepared(repository.formatTime(0))
            _playerState.value = PlayerState.Paused
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

    }

    private fun pause() {
        repository.pause()
        _playerState.value = PlayerState.Paused
    }

    override fun pauseIfNeeded() {
        if (isPlaying()) {
            pause()
        }
    }

    override fun release() {
        repository.release()
        _playerState.value = PlayerState.Prepared(repository.formatTime(0))
    }

    override fun updateCurrentTime() {
        if(isPlaying()){
        _playerState.postValue(PlayerState.TimeUpdated(repository.getCurrentTimeFormatted()))
            }
    }

    override fun isPlaying(): Boolean = repository.isPlaying()

}