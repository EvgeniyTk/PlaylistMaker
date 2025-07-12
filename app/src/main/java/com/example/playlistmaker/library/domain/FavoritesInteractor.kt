package com.example.playlistmaker.library.domain

import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesInteractor {
    fun getFavoritesTracks(): Flow<List<Track>>
    suspend fun addFavoriteTrack(track: Track)
    suspend fun deleteFavoriteTrack(track: Track)
    suspend fun isFavorite(track: Track): Boolean
}