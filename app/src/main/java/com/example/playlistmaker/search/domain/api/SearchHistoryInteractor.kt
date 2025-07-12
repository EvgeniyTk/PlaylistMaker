package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface SearchHistoryInteractor {
    suspend fun getSavedHistory(): List<Track>
    suspend fun addTrackToHistory(track: Track)
    fun saveTrackListToHistory(trackList: List<Track>)
    fun clearTrackListOfHistory()
}