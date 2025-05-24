package com.example.playlistmaker.library.ui.models


sealed interface PlaylistsState {
    object Empty: PlaylistsState
}