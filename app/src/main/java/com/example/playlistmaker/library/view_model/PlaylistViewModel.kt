package com.example.playlistmaker.library.view_model

import SingleLiveEvent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.PlaylistsInteractor
import com.example.playlistmaker.library.ui.models.PlaylistScreenState
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.util.debounce
import kotlinx.coroutines.launch

class PlaylistViewModel(
    private val playlistsInteractor: PlaylistsInteractor,
    private val sharingInteractor: SharingInteractor
    ) : ViewModel() {
    private val _screenState = MutableLiveData<PlaylistScreenState>()
    val screenState: LiveData<PlaylistScreenState> = _screenState

    private val _playlistDeleted = MutableLiveData<Boolean>()
    val playlistDeleted: LiveData<Boolean> get() = _playlistDeleted

    fun loadPlaylist(id: Int) {
        viewModelScope.launch {
            val playlist = playlistsInteractor.getPlaylistById(id)
            if (playlist != null) {
                val tracks = playlistsInteractor.getTracksByIds(playlist.trackIdList)
                _screenState.value = PlaylistScreenState.PlaylistContent(playlist, tracks)
            }
        }
    }


    private val _openTrackEvent = SingleLiveEvent<Track>()
    val openTrackEvent: LiveData<Track> get() = _openTrackEvent

    private val clickDebounce = debounce<Track>(
        delayMillis = CLICK_DEBOUNCE_DELAY,
        coroutineScope = viewModelScope,
        useLastParam = true
    ) { track ->
        _openTrackEvent.postValue(track)
    }

    fun onTrackClick(track: Track) {
        clickDebounce(track)
    }

    fun deleteTrack(trackId: Int, playlistId: Int) {
        viewModelScope.launch {
            playlistsInteractor.deleteTrackFromPlaylist(trackId, playlistId)
            loadPlaylist(playlistId)
        }
    }

    fun sharePlaylistMessage(message: String) {
        sharingInteractor.sharePlaylist(message)
    }

    fun deletePlaylist(playlistId: Int) {
        viewModelScope.launch {
            playlistsInteractor.deletePlaylist(playlistId)
            _playlistDeleted.postValue(true)
        }
    }



    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

}