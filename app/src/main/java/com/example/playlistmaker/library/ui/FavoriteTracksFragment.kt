package com.example.playlistmaker.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.example.playlistmaker.library.ui.models.FavoriteTracksState
import com.example.playlistmaker.library.view_model.FavoriteTracksViewModel
import com.example.playlistmaker.player.ui.PlayerFragment
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.TrackAdapter
import com.example.playlistmaker.util.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment: Fragment() {

    private val viewModel: FavoriteTracksViewModel by viewModel()
    private var _binding: FragmentFavoriteTracksBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: TrackAdapter
    private lateinit var onTrackClickDebounce: (Track) -> Unit

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteTracksBinding.inflate(inflater, container, false)

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onTrackClickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            findNavController().navigate(
                R.id.action_libraryFragment_to_playerFragment,
                PlayerFragment.createArgs(track)
            )
        }

        adapter = TrackAdapter {
            onTrackClickDebounce(it)
        }

        binding.favoritesRV.layoutManager = LinearLayoutManager(requireContext())
        binding.favoritesRV.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showPlaceholder(){
        binding.favoritesPlaceholderIV.visibility = View.VISIBLE
        binding.favoritesPlaceholderTV.visibility = View.VISIBLE
        binding.favoritesRV.visibility = View.GONE
    }

    private fun showContent(){
        binding.favoritesPlaceholderIV.visibility = View.GONE
        binding.favoritesPlaceholderTV.visibility = View.GONE
        binding.favoritesRV.visibility = View.VISIBLE

    }

    private fun render(state: FavoriteTracksState) {
        when (state) {
            FavoriteTracksState.Empty -> showPlaceholder()
            is FavoriteTracksState.Content -> {
                showContent()
                adapter.updateData(state.tracks)
            }
        }
    }

    companion object{
        fun newInstance(): FavoriteTracksFragment {
            return FavoriteTracksFragment()
        }
        private const val CLICK_DEBOUNCE_DELAY = 330L
    }
}