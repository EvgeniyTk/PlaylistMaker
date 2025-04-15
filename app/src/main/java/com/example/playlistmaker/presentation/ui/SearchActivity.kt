package com.example.playlistmaker.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.data.dto.ResponseType
import com.example.playlistmaker.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.models.TracksResponse


class SearchActivity : AppCompatActivity() {
    private lateinit var inputEditText: EditText
    private lateinit var searchAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    private lateinit var recycler: RecyclerView
    private lateinit var progressBar: ProgressBar

    private var searchTextValue: String = SEARCH_TEXT_VALUE


    companion object {
        const val SEARCH_TEXT_VALUE = ""
        const val SEARCH_TEXT_KEY = "SEARCH TEXT"
        const val TRACK = "TRACK"
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    enum class CodeError {
        GOOD,
        NORESULT,
        BADCONNECTION
    }

    private var trackListHistory: MutableList<Track> = mutableListOf()
    private lateinit var errorPage: LinearLayout
    private lateinit var errorImage: ImageView
    private lateinit var errorText: TextView
    private lateinit var errorButton: Button
    private lateinit var searchHistoryHeader: TextView
    private lateinit var searchHistoryClearButton: Button

    private val getTracksInteractor = Creator.provideTracksInteractor()
    private val getSearchHistoryInteractor = Creator.provideSearchHistoryInteractor()

    private val handler = Handler(Looper.getMainLooper())
    private val searchRunnable = Runnable { search() }

    private fun searchDebounce() {
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
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

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(searchRunnable)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        inputEditText = findViewById(R.id.searchInputEditText)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val clearButton = findViewById<ImageView>(R.id.clear_button)
        val backButton = findViewById<Toolbar>(R.id.toolbar_search)
        recycler = findViewById(R.id.track_list)
        errorPage = findViewById(R.id.placeholder_error)
        errorImage = findViewById(R.id.error_image)
        errorText = findViewById(R.id.error_tv)
        errorButton = findViewById(R.id.error_button)


        searchHistoryClearButton = findViewById(R.id.clear_history_button)
        searchHistoryHeader = findViewById(R.id.search_history_header)
        progressBar = findViewById(R.id.progressBar)

        recycler.layoutManager = LinearLayoutManager(this)
        searchAdapter = TrackAdapter { setTrack(it) }
        historyAdapter = TrackAdapter { setTrack(it) }
        recycler.adapter = searchAdapter


        backButton.setNavigationOnClickListener {
            finish()
        }


        searchHistoryClearButton.setOnClickListener {
            getSearchHistoryInteractor.clearTrackListOfHistory()
            updateHistoryUi()
            inputEditText.clearFocus()
        }

        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            updateHistoryUi()
            if (hasFocus && trackListHistory.isNotEmpty()) {
                setHistoryVisibility(true)
            } else {
                setHistoryVisibility(false)
            }
        }

        clearButton.setOnClickListener {
            inputEditText.setText("")
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(inputEditText.windowToken, 0)
            inputEditText.clearFocus()
            errorPage.isVisible = false
            trackListHistory.clear()
            searchAdapter.updateData(trackListHistory)
            handler.removeCallbacks(searchRunnable)
        }

        errorButton.setOnClickListener {
            search()
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()
                if (s.isNullOrEmpty()) {
                    showPlaceholder(CodeError.GOOD)
                    handler.removeCallbacks(searchRunnable)
                } else {
                    searchDebounce()
                }
                setHistoryVisibility(inputEditText.hasFocus() && s?.isEmpty() == true)
                recycler.visibility = View.VISIBLE

            }

            override fun afterTextChanged(s: Editable?) {
                searchTextValue = s.toString()

            }
        }
        inputEditText.addTextChangedListener(simpleTextWatcher)


    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT_KEY, searchTextValue)

    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchTextValue = savedInstanceState.getString(SEARCH_TEXT_KEY, SEARCH_TEXT_VALUE)
        inputEditText.setText(searchTextValue)

    }

    private fun search() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(inputEditText.windowToken, 0)
        recycler.visibility = View.GONE
        setHistoryVisibility(false)
        showPlaceholder(CodeError.GOOD)
        if (inputEditText.text.isNotEmpty()) {
            showPlaceholder(CodeError.GOOD)
            progressBar.visibility = View.VISIBLE
            getTracksInteractor.searchTracks(inputEditText.text.toString(),
                object : TracksInteractor.TracksConsumer {
                    override fun consume(foundTracks: TracksResponse) {
                        runOnUiThread {
                            progressBar.visibility = View.GONE
                            updateUi(foundTracks)

                        }
                    }

                })
        }

    }

    private fun updateUi(trackResponse: TracksResponse) {
        when (trackResponse.responseType) {
            ResponseType.SUCCESS -> {
                if (trackResponse.trackList.isNullOrEmpty()) {
                    showPlaceholder(CodeError.NORESULT)
                } else {
                    showPlaceholder(CodeError.GOOD)
                    recycler.visibility = View.VISIBLE
                    searchAdapter.updateData(trackResponse.trackList.toMutableList())
                }

            }

            ResponseType.BAD_CONNECTION -> showPlaceholder(CodeError.BADCONNECTION)
            ResponseType.UNKNOWN -> Log.d("SearchError", "Error")
        }
    }

    private fun updateHistoryUi() {
        getSearchHistoryInteractor.getSavedHistory(
            object : SearchHistoryInteractor.SearchHistoryConsumer {
                override fun consume(trackList: List<Track>) {
                    historyAdapter.updateData(trackList)
                    trackListHistory = trackList.toMutableList()
                }
            }
        )
    }

    private fun showPlaceholder(code: CodeError) {
        when (code) {
            CodeError.GOOD -> errorPage.isVisible = false
            CodeError.NORESULT -> {
                errorPage.isVisible = true
                errorImage.setImageResource(R.drawable.no_result)
                errorText.text = getString(R.string.error_result_search)
                errorButton.isVisible = false
            }

            CodeError.BADCONNECTION -> {
                errorPage.isVisible = true
                errorImage.setImageResource(R.drawable.bad_connection)
                errorText.text = getString(R.string.error_bad_connection)
                errorButton.isVisible = true
            }

        }
    }

    private fun setTrack(track: Track) {
        if (clickDebounce()) {
            getSearchHistoryInteractor.addTrackToHistory(track)
            val trackIntent = Intent(this, PlayerActivity::class.java)
            trackIntent.putExtra(TRACK, track)
            startActivity(trackIntent)
            updateHistoryUi()
        }
    }

    private fun setHistoryVisibility(isSearchFieldEmpty: Boolean) {
        if (isSearchFieldEmpty) {
            searchHistoryHeader.visibility = View.VISIBLE
            searchHistoryClearButton.visibility = View.VISIBLE
            trackListHistory.clear()
            searchAdapter.updateData(trackListHistory)
            recycler.adapter = historyAdapter
            recycler.visibility = View.VISIBLE
        } else {
            searchHistoryHeader.visibility = View.GONE
            searchHistoryClearButton.visibility = View.GONE
            recycler.adapter = searchAdapter
            recycler.visibility = View.GONE
        }
    }


}