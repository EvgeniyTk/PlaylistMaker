package com.example.playlistmaker.search.data

import android.content.SharedPreferences
import com.example.playlistmaker.db.data.AppDatabase
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.search.domain.models.Track
import com.google.gson.Gson
import androidx.core.content.edit

class SearchHistoryRepositoryImpl(
    private val sharedPref: SharedPreferences,
    private val gson: Gson,
    private val appDatabase: AppDatabase
): SearchHistoryRepository {

    companion object {
        const val HISTORY_LIST = "history_list"
    }

    override suspend fun getSavedHistoryList(): List<Track> {
        val str = sharedPref.getString(HISTORY_LIST, null)
        var trackList = mutableListOf<Track>()
        if (str != null) {
            trackList = mutableListOf<Track>().apply {
                addAll(gson.fromJson(str, Array<Track>::class.java))
            }
        }
        val favoriteIds = appDatabase.trackDao().getTracksId()
        return trackList.map {
            it.copy(isFavorite = favoriteIds.contains(it.trackId))
        }
    }

    override fun saveTrackListToHistory(trackList: List<Track>) {
        val str = gson.toJson(trackList)
        sharedPref.edit {
            putString(HISTORY_LIST, str)
        }
    }
}