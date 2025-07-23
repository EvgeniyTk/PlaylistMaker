package com.example.playlistmaker.library.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.PlaylistsInteractor
import com.example.playlistmaker.library.ui.models.PlaylistsState
import kotlinx.coroutines.launch

class PlaylistsViewModel(
    private val playlistsInteractor: PlaylistsInteractor
): ViewModel() {
    init {
        viewModelScope.launch {
            playlistsInteractor.getPlaylists().collect{
                if(it.isEmpty()){
                    renderState(PlaylistsState.Empty)
                } else {
                    renderState(PlaylistsState.Content(it))
                }
            }
        }
    }
    private val stateLiveData = MutableLiveData<PlaylistsState>()
    fun observeState(): LiveData<PlaylistsState> = stateLiveData


    private fun renderState(state: PlaylistsState) {
        stateLiveData.postValue(state)
    }
}