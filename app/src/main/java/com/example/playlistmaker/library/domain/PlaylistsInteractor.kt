package com.example.playlistmaker.library.domain

import android.net.Uri
import com.example.playlistmaker.library.domain.model.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistsInteractor {
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun addPlaylist(playlist: Playlist)
    suspend fun updatePlaylist(playlist: Playlist)
    suspend fun saveImageToPrivateStorage(uri: Uri, playlistName: String): String
}