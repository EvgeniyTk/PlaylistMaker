package com.example.playlistmaker.player.view_model


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.player.model.PlayerState
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.CoroutineScope


class PlayerViewModel(
    playerInteractorFactory: (CoroutineScope) -> PlayerInteractor
) : ViewModel() {

    private val playerInteractor = playerInteractorFactory(viewModelScope)

    private var isUrlSet = false
    private var track: Track? = null
    val playerUiState: LiveData<PlayerState> = playerInteractor.getPlayerState()

    fun setUrl(url: String) {
        if (!isUrlSet) {
            playerInteractor.setUrl(url)
            isUrlSet = true
        }
    }

    fun setTrack(newTrack: Track) {
        if (track == null) {
            track = newTrack
            setUrl(newTrack.previewUrl)
        }
    }

    fun playbackControl() {
        playerInteractor.playbackControl()
    }

    fun pauseIfNeeded() {
        playerInteractor.pauseIfNeeded()
    }

    override fun onCleared() {
        super.onCleared()
        playerInteractor.release()
    }

}
