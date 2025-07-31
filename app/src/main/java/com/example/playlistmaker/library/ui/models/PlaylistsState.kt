package com.example.playlistmaker.library.ui.models

import com.example.playlistmaker.library.domain.model.Playlist


sealed interface PlaylistsState {
    object Empty: PlaylistsState
    data class Content(val playlists: List<Playlist>): PlaylistsState
}