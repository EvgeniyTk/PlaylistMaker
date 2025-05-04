package com.example.playlistmaker.player.view_model


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.player.model.PlayerState
import com.example.playlistmaker.search.domain.models.Track


class PlayerViewModel(
    private val playerInteractor: PlayerInteractor
) : ViewModel() {

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

    companion object {
        fun getViewModelFactory(
            playerInteractor: PlayerInteractor
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlayerViewModel(playerInteractor)
            }
        }
    }
}
