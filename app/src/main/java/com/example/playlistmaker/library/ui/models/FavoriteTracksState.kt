package com.example.playlistmaker.library.ui.models

import com.example.playlistmaker.search.domain.models.Track

sealed interface FavoriteTracksState {
    object Empty: FavoriteTracksState
    data class Content(val tracks: List<Track>): FavoriteTracksState
}