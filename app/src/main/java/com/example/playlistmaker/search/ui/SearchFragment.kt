package com.example.playlistmaker.search.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity.INPUT_METHOD_SERVICE
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.main.ui.RootActivity
import com.example.playlistmaker.player.ui.PlayerFragment
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.models.TracksState
import com.example.playlistmaker.theme.AppTheme
import com.example.playlistmaker.search.view_model.SearchViewModel
import com.example.playlistmaker.util.debounce
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private val searchViewModel: SearchViewModel by viewModel()

    private val uiStateFlow = MutableStateFlow<TracksState>(TracksState.History(emptyList()))
    private val queryFlow = MutableStateFlow("")

    private lateinit var onTrackClickDebounce: (Track) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                AppTheme {
                    SearchScreen(
                        stateFlow = uiStateFlow,
                        queryFlow = searchViewModel.observeQueryFlow(),
                        onQueryChange = { text -> handleQueryChange(text) },
                        onClearQuery = { handleClearQuery() },
                        onRetry = { searchViewModel.onErrorButtonClick() },
                        onClearHistory = { searchViewModel.onClearHistory() },
                        onTrackClick = { track -> searchViewModel.setTrack(track) },
                        onInputFocusChange = { focused -> searchViewModel.onInputFocusChange(focused) }
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onTrackClickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            findNavController().navigate(
                R.id.action_searchFragment_to_playerFragment,
                PlayerFragment.createArgs(track)
            )
        }

        if (savedInstanceState != null) {
            searchViewModel.restoreInstanceState(savedInstanceState)
        }

        searchViewModel.observeState().observe(viewLifecycleOwner) { state ->
            uiStateFlow.value = state
        }

        searchViewModel.openTrackEvent.observe(viewLifecycleOwner) { track ->
            view.post {
                (activity as? RootActivity)?.animateBottomNavigationView()
                onTrackClickDebounce(track)
            }
        }

        searchViewModel.hideKeyboardEvent.observe(viewLifecycleOwner) {
            hideKeyboard()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        searchViewModel.saveInstanceState(outState)
    }

    private fun handleQueryChange(text: String) {
        queryFlow.value = text

        if (text.isEmpty()) {
            searchViewModel.updateHistory()
            searchViewModel.cancelSearchJob()
        } else {
            searchViewModel.searchDebounce(changedText = text)
        }

        searchViewModel.onSearchTextChanged(text)
    }

    private fun handleClearQuery() {
        queryFlow.value = ""
        searchViewModel.onClearInputText()
        hideKeyboard()
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        view?.let { imm.hideSoftInputFromWindow(it.windowToken, 0) }
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 300L
    }
}