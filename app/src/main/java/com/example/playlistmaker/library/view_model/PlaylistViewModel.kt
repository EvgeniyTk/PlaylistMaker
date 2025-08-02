package com.example.playlistmaker.library.view_model

import SingleLiveEvent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.PlaylistsInteractor
import com.example.playlistmaker.library.ui.models.PlaylistScreenState
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.util.debounce
import kotlinx.coroutines.launch

class PlaylistViewModel(private val playlistsInteractor: PlaylistsInteractor) : ViewModel() {
    private val _screenState = MutableLiveData<PlaylistScreenState>()
    val screenState: LiveData<PlaylistScreenState> = _screenState

    fun loadPlaylist(id: Int) {
        viewModelScope.launch {
            val playlist = playlistsInteractor.getPlaylistById(id)
            if (playlist != null) {
                playlistsInteractor.getTracksByIds(playlist.trackIdList).collect { tracks ->
                    _screenState.value = PlaylistScreenState.PlaylistContent(playlist, tracks)
                }

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


    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

}