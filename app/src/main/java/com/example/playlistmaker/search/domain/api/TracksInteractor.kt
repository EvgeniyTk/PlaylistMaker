package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.TracksResponse

interface TracksInteractor {
    fun searchTracks(expression: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(foundTracks: TracksResponse)
    }
}