package com.example.playlistmaker.library.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.library.domain.FavoritesInteractor
import com.example.playlistmaker.library.ui.models.FavoriteTracksState
import kotlinx.coroutines.launch

class FavoriteTracksViewModel(private val favoritesInteractor: FavoritesInteractor): ViewModel() {
    init {
        viewModelScope.launch {
            favoritesInteractor.getFavoritesTracks().collect{
                if(it.isEmpty()){
                    renderState(FavoriteTracksState.Empty)
                } else {
                    renderState(FavoriteTracksState.Content(it))
                }
            }
        }
    }
    private val stateLiveData = MutableLiveData<FavoriteTracksState>()
    fun observeState(): LiveData<FavoriteTracksState> = stateLiveData

    private fun renderState(state: FavoriteTracksState) {
        stateLiveData.postValue(state)
    }
}