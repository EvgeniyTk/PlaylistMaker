package com.example.playlistmaker.player.domain

import androidx.lifecycle.LiveData
import com.example.playlistmaker.player.model.PlayerState

interface PlayerInteractor {
    fun setUrl(url: String)
    fun playbackControl()
    fun pauseIfNeeded()
    fun release()
    fun getPlayerState(): LiveData<PlayerState>
}