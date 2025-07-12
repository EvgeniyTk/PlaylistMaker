package com.example.playlistmaker.library.data

import com.example.playlistmaker.db.data.converters.TrackDbConvertor
import com.example.playlistmaker.db.data.dao.TrackDao
import com.example.playlistmaker.db.data.entity.TrackEntity
import com.example.playlistmaker.library.domain.FavoritesRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesRepositoryImpl(
    private val trackDao: TrackDao,
    private val trackDbConvertor: TrackDbConvertor
) : FavoritesRepository {

    override fun getFavoritesTracks(): Flow<List<Track>> =
        trackDao.getTracks()
            .map { convertFromTrackEntity(it) }



    override suspend fun addFavoriteTrack(track: Track) {
        val trackEntity = trackDbConvertor.map(track)
        trackDao.insertTrack(trackEntity)
    }

    override suspend fun deleteFavoriteTrack(track: Track) {
        val trackEntity = trackDbConvertor.map(track)
        trackDao.deleteTrack(trackEntity)
    }

    override suspend fun isFavorite(track: Track): Boolean {
        val favoriteIds = trackDao.getTracksId()
        return favoriteIds.contains(track.trackId)
    }

    private fun convertFromTrackEntity(tracks: List<TrackEntity>): List<Track> {
        return tracks.map { track -> trackDbConvertor.map(track) }
    }
}