package com.example.playlistmaker.library.domain.impl

import com.example.playlistmaker.library.domain.FavoritesInteractor
import com.example.playlistmaker.library.domain.FavoritesRepository
import com.example.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavoritesInteractorImpl(
    private val favoritesRepository: FavoritesRepository
) : FavoritesInteractor {
    override fun getFavoritesTracks(): Flow<List<Track>> {
        return favoritesRepository.getFavoritesTracks()
    }

    override suspend fun addFavoriteTrack(track: Track) {
        favoritesRepository.addFavoriteTrack(track)
    }

    override suspend fun deleteFavoriteTrack(track: Track) {
        favoritesRepository.deleteFavoriteTrack(track)
    }

    override suspend fun isFavorite(track: Track): Boolean {
        return favoritesRepository.isFavorite(track)
    }

}
