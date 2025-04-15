package com.example.playlistmaker.data

import com.example.playlistmaker.Creator
import com.example.playlistmaker.domain.api.SearchHistoryRepository
import com.example.playlistmaker.domain.models.Track
import com.google.gson.Gson

class SearchHistoryRepositoryImpl(): SearchHistoryRepository {

    companion object {
        const val HISTORY_LIST = "history_list"

    }
    private val sharedPref = Creator.provideSharedPreferences()
    private val gson = Gson()


    override fun getSavedHistoryList(): List<Track> {
        val str = sharedPref.getString(HISTORY_LIST, null)
        var trackList = mutableListOf<Track>()
        if (str != null) {
            trackList = mutableListOf<Track>().apply {
                addAll(gson.fromJson(str, Array<Track>::class.java))
            }
        }
        return trackList
    }

    override fun saveTrackListToHistory(trackList: List<Track>) {
        val str = gson.toJson(trackList)
        sharedPref.edit()
            .putString(HISTORY_LIST, str)
            .apply()
    }
}