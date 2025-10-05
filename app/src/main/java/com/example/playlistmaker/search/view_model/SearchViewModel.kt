package com.example.playlistmaker.search.view_model

import SingleLiveEvent
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.search.data.dto.ResponseType
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.models.TracksResponse
import com.example.playlistmaker.search.ui.models.TracksState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch


class SearchViewModel(
    private val getTracksInteractor: TracksInteractor,
    private val getSearchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {
    private var searchTextValue: String = SEARCH_TEXT_VALUE

    private val stateLiveData = MutableLiveData<TracksState>()
    fun observeState(): LiveData<TracksState> = stateLiveData

    private val _stateFlow = MutableStateFlow<TracksState>(TracksState.Loading)
    fun observeStateFlow(): StateFlow<TracksState> = _stateFlow

    private val _queryFlow = MutableStateFlow(SEARCH_TEXT_VALUE)
    fun observeQueryFlow(): StateFlow<String> = _queryFlow

    private fun renderState(state: TracksState) {
        stateLiveData.postValue(state)
        _stateFlow.value = state
    }

    private val _hideKeyboardEvent = SingleLiveEvent<Boolean>()
    val hideKeyboardEvent: LiveData<Boolean> get() = _hideKeyboardEvent

    private fun hideKeyboard() {
        _hideKeyboardEvent.value = true
    }

    private var isInputFocused: Boolean = false


    enum class CodeError {
        NORESULT,
        BADCONNECTION
    }

    private var trackListHistory: MutableList<Track> = mutableListOf()
    private var lastSearchResult: MutableList<Track> = mutableListOf()

    private var lastSearchText: String? = null

    private var searchJob: Job? = null

    fun searchDebounce(changedText: String) {
        if (lastSearchText == changedText) {
            return
        }

        lastSearchText = changedText
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            search(changedText)
        }
    }

    private var isClickAllowed = true


    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewModelScope.launch {
                delay(CLICK_DEBOUNCE_DELAY)
                isClickAllowed = true
            }
        }
        return current
    }

    fun cancelSearchJob() {
        searchJob?.cancel()
        searchJob = null
    }


    fun onClearHistory() {
        getSearchHistoryInteractor.clearTrackListOfHistory()
        updateHistory()
    }

    fun onClearInputText() {
        searchTextValue = ""
        _queryFlow.value = ""
        renderState(TracksState.History(trackListHistory))
    }


    fun onInputFocusChange(hasFocus: Boolean) {
        if (isInputFocused == hasFocus) return

        isInputFocused = hasFocus
        if (hasFocus && searchTextValue.isEmpty()) {
            updateHistory()
        } else {

            if (lastSearchResult.isNotEmpty()) {
                renderState(TracksState.Content(lastSearchResult))
            }
        }
    }

    fun onErrorButtonClick() {
        search(lastSearchText ?: "")
    }

    fun onSearchTextChanged(newText: String) {
        searchTextValue = newText
        _queryFlow.value = newText
    }


    fun saveInstanceState(outState: Bundle) {
        outState.putString(SEARCH_TEXT_KEY, searchTextValue)
    }

    fun restoreInstanceState(savedInstanceState: Bundle) {
        searchTextValue = savedInstanceState.getString(SEARCH_TEXT_KEY, SEARCH_TEXT_VALUE)
        _queryFlow.value = searchTextValue
    }


    private fun search(newSearchText: String) {
        hideKeyboard()
        lastSearchText = newSearchText
        if (newSearchText.isNotEmpty()) {
            renderState(TracksState.Loading)
            viewModelScope.launch {
                getTracksInteractor
                    .searchTracks(newSearchText)
                    .catch {e ->
                        Log.e("SearchViewModel", "Unexpected error", e)
                        renderState(TracksState.Error(CodeError.BADCONNECTION))
                    }
                    .collect{foundTracks ->
                        updateUi(foundTracks)
                    }
            }
        } else {
            renderState(TracksState.Error(CodeError.NORESULT))
        }
    }


    private fun updateUi(trackResponse: TracksResponse) {
        when (trackResponse.responseType) {
            ResponseType.SUCCESS -> {
                if (trackResponse.trackList.isNullOrEmpty()) {
                    renderState(TracksState.Error(CodeError.NORESULT))
                } else {
                    lastSearchResult = trackResponse.trackList.toMutableList()
                    renderState(TracksState.Content(trackResponse.trackList))

                }

            }

            ResponseType.BAD_CONNECTION -> renderState(TracksState.Error(CodeError.BADCONNECTION))
            ResponseType.UNKNOWN -> Log.d("SearchError", "Error")
            ResponseType.BAD_REQUEST -> renderState(TracksState.Error(CodeError.NORESULT))
        }
    }

    fun updateHistory() {
        viewModelScope.launch {
            val trackList = getSearchHistoryInteractor.getSavedHistory()
            trackListHistory = trackList.toMutableList()
            renderState(TracksState.History(trackListHistory))
        }
    }

    private val _openTrackEvent = SingleLiveEvent<Track>()
    val openTrackEvent: LiveData<Track> get() = _openTrackEvent

    fun setTrack(track: Track) {
        if (clickDebounce()) {
            viewModelScope.launch {
                getSearchHistoryInteractor.addTrackToHistory(track)
                _openTrackEvent.postValue(track)
                if (trackListHistory.contains(track)) {
                    if (searchTextValue.isEmpty()) {
                        updateHistory()
                    }
                }
            }

        }
    }
    fun currentQuery(): String = searchTextValue


    companion object {
        const val SEARCH_TEXT_VALUE = ""
        const val SEARCH_TEXT_KEY = "SEARCH TEXT"

        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L

    }

}