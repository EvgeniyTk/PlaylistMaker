package com.example.playlistmaker.player.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.FavoritesInteractor
import com.example.playlistmaker.library.domain.PlaylistsInteractor
import com.example.playlistmaker.library.domain.model.Playlist
import com.example.playlistmaker.player.model.AddTrackStatus
import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.player.model.PlayerState
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val playerInteractor: PlayerInteractor,
    private val favoritesInteractor: FavoritesInteractor,
    private val playlistInteractor: PlaylistsInteractor
) : ViewModel() {

    private var timerJob: Job? = null
    private var isAppInBackground = false

    private var isUrlSet = false
    private var track: Track? = null
    val playerUiState: LiveData<PlayerState> = playerInteractor.getPlayerState()

    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlists: LiveData<List<Playlist>> = _playlists

    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite

    private val _addTrackStatus = MutableLiveData<AddTrackStatus>()
    val addTrackStatus: LiveData<AddTrackStatus> = _addTrackStatus

    fun addTrackToPlaylist(playlist: Playlist) {
        val currentTrack = track ?: return
        if (playlist.trackIdList.contains(currentTrack.trackId)) {
            _addTrackStatus.value = AddTrackStatus.AlreadyAdded(playlist.playlistName)
            return
        } else {
            viewModelScope.launch {
                playlistInteractor.updatePlaylist(currentTrack, playlist.playlistId)
                _addTrackStatus.value = AddTrackStatus.Success(playlist.playlistName)
            }
        }
    }

    private fun startTimer() {
        stopTimer()
        timerJob = viewModelScope.launch {
            while (playerInteractor.isPlaying()) {
                delay(TIME_UPDATE_INTERVAL)
                playerInteractor.updateCurrentTime()
            }
        }
    }

    fun onFavoriteClicked() {
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

    fun onPlaylistButtonClicked() {
        viewModelScope.launch {
            playlistInteractor.getPlaylists().collect {
                _playlists.postValue(it)
            }
        }
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
            viewModelScope.launch {
                _isFavorite.value = favoritesInteractor.isFavorite(newTrack)
            }
            setUrl(newTrack.previewUrl)
        }
    }

    fun playbackControl() {
        playerInteractor.playbackControl()
        if (playerInteractor.isPlaying()) {
            startTimer()
            if (isAppInBackground) {
                playerInteractor.startForegroundIfPlaying()
            }
        } else {
            stopTimer()
            playerInteractor.stopForegroundIfAny()
        }
    }

    fun pauseIfNeeded() {
        playerInteractor.pauseIfNeeded()
    }

    override fun onCleared() {
        super.onCleared()
        stopTimer()
        playerInteractor.stop()
        playerInteractor.release()
    }

    fun updateNotificationMetaIfAny(track: Track?) {
        if (track != null) {
            playerInteractor.setNotificationMeta(track.artistName, track.trackName)
        }
    }

    fun onUiStarted() {
        isAppInBackground = false
        playerInteractor.bindService()
        playerInteractor.stopForegroundIfAny()

        viewModelScope.launch {
            delay(100)
            refreshUiState()
            if (playerInteractor.isPlaying()) {
                startTimer()
            }
        }
    }

    fun onUiStopped() {
        isAppInBackground = true
        stopTimer()
        if (playerInteractor.isPlaying()) {
            playerInteractor.startForegroundIfPlaying()
        }
    }

    fun refreshUiState() {
        if (playerInteractor.isPlaying()) {
            playerInteractor.updateCurrentTime()
        }

        playerInteractor.refreshUiState()
    }

    fun onUiDestroyed() {
        playerInteractor.stop()
        playerInteractor.unbindService()
    }

    companion object {
        private const val TIME_UPDATE_INTERVAL = 330L
    }
}