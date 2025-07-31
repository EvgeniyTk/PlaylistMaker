package com.example.playlistmaker.library.domain.impl

import android.net.Uri
import com.example.playlistmaker.library.domain.PlaylistsInteractor
import com.example.playlistmaker.library.domain.PlaylistsRepository
import com.example.playlistmaker.library.domain.model.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class PlaylistsInteractorImpl(
    private val playlistsRepository: PlaylistsRepository
): PlaylistsInteractor {
    override fun getPlaylists(): Flow<List<Playlist>> {
        return playlistsRepository.getPlaylists()
    }

    override suspend fun addPlaylist(playlist: Playlist) {
        return playlistsRepository.addPlaylists(playlist)
    }

    override suspend fun updatePlaylist(track: Track, playlistId: Int) {
        return playlistsRepository.updatePlaylist(track, playlistId)
    }

    override suspend fun saveImageToPrivateStorage(
        uri: Uri,
        playlistName: String
    ): String {
        return playlistsRepository.saveImageToPrivateStorage(uri, playlistName)
    }


}