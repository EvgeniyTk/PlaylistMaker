package com.example.playlistmaker.player.domain

import androidx.lifecycle.LiveData
import com.example.playlistmaker.player.model.PlayerState

interface PlayerInteractor {
    fun getPlayerState(): LiveData<PlayerState>
    fun setUrl(url: String)
    fun playbackControl()
    fun pauseIfNeeded()
    fun stop()
    fun release()
    fun updateCurrentTime()
    fun setNotificationMeta(artist: String, title: String)
    fun startForegroundIfPlaying()
    fun stopForegroundIfAny()
    fun isPlaying(): Boolean
    fun refreshUiState()
    fun bindService()
    fun unbindService()
}