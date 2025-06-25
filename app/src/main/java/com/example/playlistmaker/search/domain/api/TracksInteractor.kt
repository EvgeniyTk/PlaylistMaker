package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.TracksResponse
import kotlinx.coroutines.flow.Flow

interface TracksInteractor {
    fun searchTracks(expression: String): Flow<TracksResponse>
}