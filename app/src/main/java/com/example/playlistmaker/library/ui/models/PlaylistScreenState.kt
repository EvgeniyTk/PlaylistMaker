package com.example.playlistmaker.library.ui.models

import com.example.playlistmaker.library.domain.model.Playlist
import com.example.playlistmaker.search.domain.models.Track

sealed interface PlaylistScreenState {
    data class PlaylistEmptyTracks(val playlist: Playlist): PlaylistScreenState
    data class PlaylistContent(
        val playlist: Playlist,
        val tracks: List<Track>
    ): PlaylistScreenState
}