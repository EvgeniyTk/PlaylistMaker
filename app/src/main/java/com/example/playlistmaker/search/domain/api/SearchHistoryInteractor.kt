package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface SearchHistoryInteractor {
    fun getSavedHistory(consumer: SearchHistoryConsumer)
    fun addTrackToHistory(track: Track)
    fun saveTrackListToHistory(trackList: List<Track>)
    fun clearTrackListOfHistory()

    interface SearchHistoryConsumer {
        fun consume(trackList: List<Track>)
    }

}