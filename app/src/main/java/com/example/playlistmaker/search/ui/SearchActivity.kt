package com.example.playlistmaker.search.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivitySearchBinding
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.view_model.SearchViewModel
import com.example.playlistmaker.search.view_model.SearchViewModel.CodeError
import com.example.playlistmaker.search.ui.models.TracksState
import com.example.playlistmaker.util.App

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding

    private lateinit var searchViewModel: SearchViewModel

    private lateinit var adapter: TrackAdapter

    private var simpleTextWatcher: TextWatcher? = null


    companion object {
        const val TRACK = "TRACK"
    }

    override fun onDestroy() {
        super.onDestroy()
        simpleTextWatcher?.let { binding.searchInputEditText.removeTextChangedListener(it) }


        if (isFinishing) {
            (this.application as? App)?.searchViewModel = null
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()


        searchViewModel = ViewModelProvider(
            this,
            SearchViewModel.getViewModelFactory()
        )[SearchViewModel::class.java]

        searchViewModel.observeState().observe(this) {
            render(it)
        }

        searchViewModel.hideKeyboardEvent.observe(this) {
            hideKeyboard()
        }

        searchViewModel.observeShowHistoryPage().observe(this) {
            showHistoryPage(it)
        }


        adapter = TrackAdapter { searchViewModel.setTrack(it) }

        binding.trackList.layoutManager = LinearLayoutManager(this)
        binding.trackList.adapter = adapter

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.search)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (savedInstanceState != null) {
            searchViewModel.restoreInstanceState(savedInstanceState)
        }

        simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.clearButton.isVisible = !s.isNullOrEmpty()
                if (s.isNullOrEmpty()) {
                    showContent(emptyList())
                    searchViewModel.removeCallbacks()
                } else {
                    searchViewModel.searchDebounce(
                        changedText = s.toString()
                    )
                }
                searchViewModel.setHistoryVisibility(binding.searchInputEditText.hasFocus() && s?.isEmpty() == true)
                binding.trackList.visibility = View.VISIBLE

            }

            override fun afterTextChanged(s: Editable?) {
                searchViewModel.onSearchTextChanged(s.toString())

            }
        }

        simpleTextWatcher?.let { binding.searchInputEditText.addTextChangedListener(it) }

        binding.toolbarSearch.setNavigationOnClickListener {
            finish()
        }

        binding.clearHistoryButton.setOnClickListener {
            searchViewModel.onClearHistory()
            binding.searchInputEditText.clearFocus()
        }

        binding.clearButton.setOnClickListener {
            binding.searchInputEditText.setText("")
            searchViewModel.onClearInputText()
            hideKeyboard()
            binding.searchInputEditText.clearFocus()
        }

        binding.errorButton.setOnClickListener {
            searchViewModel.onErrorButtonClick()
        }

        binding.searchInputEditText.setOnFocusChangeListener { _, hasFocus ->
            searchViewModel.onInputFocusChange(hasFocus)
        }


    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        searchViewModel.saveInstanceState(outState)

    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.placeholderError.visibility = View.GONE
        binding.errorButton.visibility = View.GONE
        binding.trackList.visibility = View.GONE
    }

    private fun showError(code: CodeError) {
        binding.placeholderError.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
        binding.trackList.visibility = View.GONE

        when (code) {
            CodeError.NORESULT -> {
                binding.errorImage.setImageResource(R.drawable.no_result)
                binding.errorTv.text = getString(R.string.error_result_search)
                binding.errorButton.visibility = View.GONE
            }

            CodeError.BADCONNECTION -> {
                binding.errorImage.setImageResource(R.drawable.bad_connection)
                binding.errorTv.text = getString(R.string.error_bad_connection)
                binding.errorButton.visibility = View.VISIBLE
            }
        }
    }

    fun showContent(list: List<Track>) {
        binding.progressBar.visibility = View.GONE
        binding.placeholderError.visibility = View.GONE
        binding.errorButton.visibility = View.GONE
        showTracks(list)

    }

    private fun showHistoryPage(isVisible: Boolean) {
        binding.clearHistoryButton.visibility = if (isVisible) View.VISIBLE else View.GONE
        binding.searchHistoryHeader.visibility = if (isVisible) View.VISIBLE else View.GONE
    }


    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }

    private fun render(state: TracksState) {
        when (state) {
            is TracksState.Loading -> showLoading()
            is TracksState.Content -> showContent(state.list)
            is TracksState.Error -> showError(state.code)
        }
    }

    private fun showTracks(trackList: List<Track>) {
        adapter.updateData(trackList)
        binding.trackList.visibility = if (trackList.isEmpty()) View.GONE else View.VISIBLE
    }


}