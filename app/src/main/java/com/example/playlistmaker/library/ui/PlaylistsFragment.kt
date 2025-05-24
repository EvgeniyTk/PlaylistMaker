package com.example.playlistmaker.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentPlaylistsBinding
import com.example.playlistmaker.library.ui.models.PlaylistsState
import com.example.playlistmaker.library.view_model.PlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment: Fragment() {

    companion object{
        fun newInstance(): PlaylistsFragment {
            return PlaylistsFragment()
        }
    }

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

        viewModel.setState(PlaylistsState.Empty) // заглушка

        return binding.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showPlaceholder(){
        binding.newPlaylistButton.visibility = View.VISIBLE
        binding.playlistsPlaceholderIV.visibility = View.VISIBLE
        binding.playlistsPlaceholderTV.visibility = View.VISIBLE
    }

    private fun render(state: PlaylistsState) {
        when (state) {
            PlaylistsState.Empty -> showPlaceholder()
        }
    }
}