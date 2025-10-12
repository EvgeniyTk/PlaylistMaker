package com.example.playlistmaker.library.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.PlaylistsInteractor
import com.example.playlistmaker.library.ui.models.PlaylistsState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val playlistsInteractor: PlaylistsInteractor
): ViewModel() {

    private val _state = MutableStateFlow<PlaylistsState>(PlaylistsState.Empty)
    val state: StateFlow<PlaylistsState> = _state.asStateFlow()

    private val stateLiveData = MutableLiveData<PlaylistsState>()
    fun observeState(): LiveData<PlaylistsState> = stateLiveData

    init {
        viewModelScope.launch {
            playlistsInteractor.getPlaylists().collect { playlists ->
                val newState = if (playlists.isEmpty()) {
                    PlaylistsState.Empty
                } else {
                    PlaylistsState.Content(playlists)
                }
                _state.value = newState
                stateLiveData.postValue(newState)
            }
        }
    }
}