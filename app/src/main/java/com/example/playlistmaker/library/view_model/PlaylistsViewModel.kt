package com.example.playlistmaker.library.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.library.ui.models.PlaylistsState

class PlaylistsViewModel: ViewModel() {
    private val stateLiveData = MutableLiveData<PlaylistsState>()
    fun observeState(): LiveData<PlaylistsState> = stateLiveData


    private fun renderState(state: PlaylistsState) {
        stateLiveData.postValue(state)
    }

    fun setState(state: PlaylistsState){
        renderState(state)
    }
}