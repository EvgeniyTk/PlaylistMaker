package com.example.playlistmaker.library.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import com.example.playlistmaker.db.data.converters.PlaylistDbConverter
import com.example.playlistmaker.db.data.converters.PlaylistTrackDbConverter
import com.example.playlistmaker.db.data.dao.PlaylistDao
import com.example.playlistmaker.db.data.dao.TrackInPlaylistDao
import com.example.playlistmaker.db.data.entity.PlaylistEntity
import com.example.playlistmaker.library.domain.PlaylistsRepository
import com.example.playlistmaker.library.domain.model.Playlist
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

class PlaylistsRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val playlistDbConverter: PlaylistDbConverter,
    private val trackInPlaylistDao: TrackInPlaylistDao,
    private val playlistTrackDbConverter: PlaylistTrackDbConverter,
    private val context: Context
): PlaylistsRepository {
    override fun getPlaylists(): Flow<List<Playlist>> =
        playlistDao.getPlaylists()
            .map { convertFromPlaylistEntity(it) }

    override suspend fun addPlaylists(playlist: Playlist) {
        val playlistEntity = playlistDbConverter.map(playlist)
        playlistDao.insertPlaylist(playlistEntity)
    }

    override suspend fun saveImageToPrivateStorage(
        uri: Uri,
        playlistName: String
    ): String = withContext(Dispatchers.IO)  {
        val filePath =
            File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), playlistName)
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val file = File(filePath, "$playlistName.jpg")
        context.contentResolver.openInputStream(uri).use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                BitmapFactory
                    .decodeStream(inputStream)
                    .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
            }
        }
        file.absolutePath
    }

    override suspend fun updatePlaylist(track: Track, playlist: Playlist) {
        val updatedTrackIdList = playlist.trackIdList.toMutableList().apply { add(track.trackId) }
        val updatedTracksCount = playlist.tracksCount + 1

        val playlistTrackEntity = playlistTrackDbConverter.map(track)
        trackInPlaylistDao.insertTrackInPlaylist(playlistTrackEntity)

        val updatedPlaylist = playlist.copy(
            trackIdList = updatedTrackIdList,
            tracksCount = updatedTracksCount
        )
        val updatedPlaylistEntity = playlistDbConverter.map(updatedPlaylist)
        playlistDao.updatePlaylist(updatedPlaylistEntity)
    }

    private fun convertFromPlaylistEntity(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map{ playlistDbConverter.map(it) }
    }
}