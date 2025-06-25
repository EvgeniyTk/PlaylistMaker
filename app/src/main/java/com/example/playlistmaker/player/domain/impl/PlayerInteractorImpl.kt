package com.example.playlistmaker.player.domain.impl

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.player.domain.PlayerRepository
import com.example.playlistmaker.player.model.PlayerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerInteractorImpl(
    private val repository: PlayerRepository,
    private val coroutineScope: CoroutineScope
) : PlayerInteractor {

    private val _playerState = MutableLiveData<PlayerState>()
    private var timerJob: Job? = null

    private fun startTimer() {
        stopTimer()
        timerJob = coroutineScope.launch {
            while (repository.isPlaying()) {
                delay(330L)
                _playerState.postValue(PlayerState.TimeUpdated(repository.getCurrentTimeFormatted()))
            }
        }
    }

    private fun stopTimer(){
        timerJob?.cancel()
        timerJob = null
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
            stopTimer()
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
        startTimer()
    }

    private fun pause() {
        repository.pause()
        _playerState.value = PlayerState.Paused
        stopTimer()
    }

    override fun pauseIfNeeded() {
        if (repository.isPlaying()) {
            pause()
        }
    }

    override fun release() {
        repository.release()
        stopTimer()
        _playerState.value = PlayerState.Prepared("00:00")
    }
}