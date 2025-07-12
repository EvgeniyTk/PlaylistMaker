package com.example.playlistmaker.player.view_model


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.FavoritesInteractor
import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.player.model.PlayerState
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val favoritesInteractor: FavoritesInteractor
) : ViewModel() {

    private var timerJob: Job? = null

    private var isUrlSet = false
    private var track: Track? = null
    val playerUiState: LiveData<PlayerState> = playerInteractor.getPlayerState()

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite

    private fun startTimer() {
        stopTimer()
        timerJob = viewModelScope.launch {
            while (playerInteractor.isPlaying()) {
                delay(TIME_UPDATE_INTERVAL)
                playerInteractor.updateCurrentTime()
            }
        }
    }

    fun onFavoriteClicked(){
        val currentTrack = track ?: return
        if (_isFavorite.value == false) {
            viewModelScope.launch {
                favoritesInteractor.addFavoriteTrack(currentTrack)
                _isFavorite.postValue(true)
            }
        } else {
            viewModelScope.launch {
                favoritesInteractor.deleteFavoriteTrack(currentTrack)
                _isFavorite.postValue(false)
            }

        }
        currentTrack.isFavorite = _isFavorite.value ?: false

    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }

    fun setUrl(url: String) {
        if (!isUrlSet) {
            playerInteractor.setUrl(url)
            isUrlSet = true
        }
    }


    fun setTrack(newTrack: Track) {
        if (track == null) {
            track = newTrack
            _isFavorite.value = newTrack.isFavorite
            setUrl(newTrack.previewUrl)
        }
    }

    fun playbackControl() {
        playerInteractor.playbackControl()
        if (playerInteractor.isPlaying()) {
            startTimer()
        } else {
            stopTimer()
        }
    }

    fun pauseIfNeeded() {
        playerInteractor.pauseIfNeeded()
    }

    override fun onCleared() {
        super.onCleared()
        playerInteractor.release()
        stopTimer()
    }

    companion object {
        private const val TIME_UPDATE_INTERVAL = 330L
    }

}