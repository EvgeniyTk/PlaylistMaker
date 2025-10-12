package com.example.playlistmaker.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.library.domain.model.Playlist
import com.example.playlistmaker.library.ui.models.PlaylistsState
import com.example.playlistmaker.library.view_model.PlaylistsViewModel
import com.example.playlistmaker.theme.AppTheme
import com.example.playlistmaker.util.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    private lateinit var onPlaylistClickDebounce: (Playlist) -> Unit
    private val viewModel: PlaylistsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val state by viewModel.observeState().observeAsState(initial = PlaylistsState.Empty)
                AppTheme {
                    PlaylistsScreen(
                        state = state,
                        onNewPlaylistClick = {
                            findNavController().navigate(R.id.action_libraryFragment_to_newPlaylistFragment)
                        },
                        onPlaylistClick = onPlaylistClickDebounce
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onPlaylistClickDebounce = debounce<Playlist>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { playlist ->
            val bundle = Bundle().apply {
                putInt(KEY, playlist.playlistId)
            }
            findNavController().navigate(
                R.id.action_libraryFragment_to_playlistFragment,
                bundle
            )
        }
    }

    companion object {
        fun newInstance(): PlaylistsFragment {
            return PlaylistsFragment()
        }
        private const val CLICK_DEBOUNCE_DELAY = 330L
        const val KEY = "playlistId"
    }
}