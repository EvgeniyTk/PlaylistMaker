package com.example.playlistmaker.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.library.domain.model.Playlist
import com.example.playlistmaker.library.ui.models.PlaylistsState
import com.example.playlistmaker.library.view_model.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {

    private lateinit var adapter: PlaylistAdapter

    private val viewModel: PlaylistsViewModel by viewModel()
    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.newPlaylistButton.setOnClickListener {
            findNavController().navigate(R.id.action_libraryFragment_to_newPlaylistFragment)
        }

        adapter = PlaylistAdapter()
        binding.playlistsRv.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.playlistsRv.adapter = adapter
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showPlaceholder() {
        binding.newPlaylistButton.isVisible = true
        binding.playlistsPlaceholderIV.isVisible = true
        binding.playlistsPlaceholderTV.isVisible = true
        binding.playlistsRv.isVisible = false
    }

    private fun render(state: PlaylistsState) {
        when (state) {
            is PlaylistsState.Empty -> showPlaceholder()
            is PlaylistsState.Content -> showContent(state.playlists)
        }
    }

    private fun showContent(list: List<Playlist>) {
        binding.newPlaylistButton.isVisible = true
        binding.playlistsPlaceholderIV.isVisible = false
        binding.playlistsPlaceholderTV.isVisible = false
        showPlaylists(list)
    }

    private fun showPlaylists(list: List<Playlist>) {
        adapter.updateData(list)
        binding.playlistsRv.isVisible = list.isNotEmpty()
    }

    companion object {
        fun newInstance(): PlaylistsFragment {
            return PlaylistsFragment()
        }
    }
}