package com.example.playlistmaker.search.ui.models

import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.view_model.SearchViewModel

sealed interface TracksState {
    object Loading: TracksState

    data class Content(val list: List<Track>): TracksState

    data class Error(
        val code: SearchViewModel.CodeError
    ): TracksState

}