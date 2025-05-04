package com.example.playlistmaker.search.view_model

import SingleLiveEvent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.search.data.dto.ResponseType
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.domain.models.TracksResponse
import com.example.playlistmaker.search.ui.models.TracksState


class SearchViewModel(
    private val getTracksInteractor: TracksInteractor,
    private val getSearchHistoryInteractor: SearchHistoryInteractor
) : ViewModel() {
    private var searchTextValue: String = SEARCH_TEXT_VALUE

    private val stateLiveData = MutableLiveData<TracksState>()
    fun observeState(): LiveData<TracksState> = stateLiveData

    private fun renderState(state: TracksState) {
        stateLiveData.postValue(state)
    }

    private val _hideKeyboardEvent = SingleLiveEvent<Boolean>()
    val hideKeyboardEvent: LiveData<Boolean> get() = _hideKeyboardEvent

    private fun hideKeyboard() {
        _hideKeyboardEvent.value = true
    }
    private var isInputFocused: Boolean = false


    companion object {
        const val SEARCH_TEXT_VALUE = ""
        const val SEARCH_TEXT_KEY = "SEARCH TEXT"

        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        private val SEARCH_REQUEST_TOKEN = Any()

        fun getViewModelFactory(
            tracksInteractor: TracksInteractor,
            searchHistoryInteractor: SearchHistoryInteractor
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchViewModel(tracksInteractor, searchHistoryInteractor)
            }
        }
    }

    enum class CodeError {
        NORESULT,
        BADCONNECTION
    }

    private var trackListHistory: MutableList<Track> = mutableListOf()
    private var lastSearchResult: MutableList<Track> = mutableListOf()

    private var lastSearchText: String? = null


    private val handler = Handler(Looper.getMainLooper())

    fun searchDebounce(changedText: String) {
        if (lastSearchText == changedText) {
            return
        }
        this.lastSearchText = changedText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        val searchRunnable = Runnable { search(changedText) }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            handler.postDelayed(
                searchRunnable,
                SEARCH_REQUEST_TOKEN,
                SEARCH_DEBOUNCE_DELAY
            )
        } else {
            val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
            handler.postAtTime(
                searchRunnable,
                SEARCH_REQUEST_TOKEN,
                postTime,
            )
        }
    }

    private var isClickAllowed = true


    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    override fun onCleared() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }


    fun onClearHistory() {
        getSearchHistoryInteractor.clearTrackListOfHistory()
        updateHistory()
    }

    fun removeCallbacks() {
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }

    fun onClearInputText() {
        renderState(TracksState.History(trackListHistory))
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)
    }


    fun onInputFocusChange(hasFocus: Boolean) {
        if (isInputFocused == hasFocus) return

        isInputFocused = hasFocus
        updateHistory()
        if (lastSearchResult.isNotEmpty() && searchTextValue.isNotEmpty()) {
            renderState(TracksState.Content(lastSearchResult))
            return
        }
        if (hasFocus && trackListHistory.isNotEmpty() && searchTextValue.isEmpty()) {
            renderState(TracksState.History(trackListHistory))
        }
    }

    fun onErrorButtonClick() {
        search(lastSearchText ?: "")
    }

    fun onSearchTextChanged(newText: String) {
        searchTextValue = newText
    }


    fun saveInstanceState(outState: Bundle) {
        outState.putString(SEARCH_TEXT_KEY, searchTextValue)
    }

    fun restoreInstanceState(savedInstanceState: Bundle) {
        searchTextValue = savedInstanceState.getString(SEARCH_TEXT_KEY, SEARCH_TEXT_VALUE)
    }


    private fun search(newSearchText: String) {
        hideKeyboard()
        lastSearchText = newSearchText
        if (newSearchText.isNotEmpty()) {
            renderState(TracksState.Loading)
            getTracksInteractor.searchTracks(newSearchText,
                object : TracksInteractor.TracksConsumer {
                    override fun consume(foundTracks: TracksResponse) {
                        updateUi(foundTracks)
                    }
                })
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
        getSearchHistoryInteractor.getSavedHistory(
            object : SearchHistoryInteractor.SearchHistoryConsumer {
                override fun consume(trackList: List<Track>) {
                    trackListHistory = trackList.toMutableList()
                    renderState(TracksState.History(trackListHistory))
                }
            }
        )
    }
    private val _openTrackEvent = SingleLiveEvent<Track>()
    val openTrackEvent: LiveData<Track> get() = _openTrackEvent

    fun setTrack(track: Track) {
        if (clickDebounce()) {
            getSearchHistoryInteractor.addTrackToHistory(track)
            _openTrackEvent.postValue(track)
            if (trackListHistory.contains(track)) {
                if(searchTextValue.isEmpty()){
                    updateHistory()
                }
            }
        }
    }

}