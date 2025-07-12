package com.example.playlistmaker.search.domain.impl

import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.models.Track

class SearchHistoryInteractorImpl(private val repository: SearchHistoryRepository):
    SearchHistoryInteractor {

    companion object {
        const val HISTORY_SIZE = 10
    }

    override suspend fun getSavedHistory(): List<Track> {
        return repository.getSavedHistoryList()
    }

    override suspend fun addTrackToHistory(track: Track) {
        val trackList = repository.getSavedHistoryList().toMutableList()
        if (getIndexOfTrack(track.trackId, trackList) != null){
            trackList.remove(track)
        } else if (trackList.size >= HISTORY_SIZE) {
            trackList.removeAt(trackList.lastIndex)
        }
        trackList.add(0, track)
        saveTrackListToHistory(trackList)

    }


    override fun saveTrackListToHistory(trackList: List<Track>) {
        repository.saveTrackListToHistory(trackList)
    }

    override fun clearTrackListOfHistory() {
        saveTrackListToHistory(emptyList<Track>())
    }

    private fun getIndexOfTrack(trackId: Int, trackList: MutableList<Track>): Int? {
        for ((index, track) in trackList.withIndex()) {
            if (track.trackId == trackId) return index
        }
        return null
    }

}