package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.models.TracksResponse
import kotlinx.coroutines.flow.Flow

class TracksInteractorImpl(private val repository: TracksRepository): TracksInteractor {

    override fun searchTracks(expression: String): Flow<TracksResponse> {
        return repository.searchTracks(expression)
    }

}