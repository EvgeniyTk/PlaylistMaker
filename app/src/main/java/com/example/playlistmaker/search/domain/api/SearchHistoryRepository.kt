package com.example.playlistmaker.search.domain.api

import com.example.playlistmaker.search.domain.models.Track

interface SearchHistoryRepository {
    fun getSavedHistoryList(): List<Track>
    fun saveTrackListToHistory(trackList: List<Track>)
}