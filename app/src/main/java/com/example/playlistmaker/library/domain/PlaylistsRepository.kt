package com.example.playlistmaker.library.domain

import android.net.Uri
import com.example.playlistmaker.library.domain.model.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistsRepository {
    fun getPlaylists(): Flow<List<Playlist>>
    suspend fun addPlaylists(playlist: Playlist)
    suspend fun saveImageToPrivateStorage(uri: Uri, playlistName: String): String
    suspend fun updatePlaylist(track: Track, playlistId: Int)
}