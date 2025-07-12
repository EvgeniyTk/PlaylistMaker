package com.example.playlistmaker.library.data

import com.example.playlistmaker.db.data.AppDatabase
import com.example.playlistmaker.db.data.converters.TrackDbConvertor
import com.example.playlistmaker.db.data.entity.TrackEntity
import com.example.playlistmaker.library.domain.FavoritesRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesRepositoryImpl(
    private val appDatabase: AppDatabase,
    private val trackDbConvertor: TrackDbConvertor
) : FavoritesRepository {

    override fun getFavoritesTracks(): Flow<List<Track>> =
        appDatabase.trackDao().getTracks()
            .map { convertFromTrackEntity(it) }



    override suspend fun addFavoriteTrack(track: Track) {
        val trackEntity = trackDbConvertor.map(track)
        appDatabase.trackDao().insertTrack(trackEntity)
    }

    override suspend fun deleteFavoriteTrack(track: Track) {
        val trackEntity = trackDbConvertor.map(track)
        appDatabase.trackDao().deleteTrack(trackEntity)
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConvertor.map(track) }
    }
}