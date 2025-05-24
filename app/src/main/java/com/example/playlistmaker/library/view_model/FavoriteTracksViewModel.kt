package com.example.playlistmaker.library.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.library.ui.models.FavoriteTracksState

class FavoriteTracksViewModel: ViewModel() {
    private val stateLiveData = MutableLiveData<FavoriteTracksState>()
    fun observeState(): LiveData<FavoriteTracksState> = stateLiveData

    private fun renderState(state: FavoriteTracksState) {
        stateLiveData.postValue(state)
    }

    fun setState(state: FavoriteTracksState){
        renderState(state)
    }
}