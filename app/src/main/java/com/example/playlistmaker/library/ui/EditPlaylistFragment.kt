package com.example.playlistmaker.library.ui

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.net.toUri
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.library.view_model.EditPlaylistViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.getValue

class EditPlaylistFragment: NewPlaylistFragment() {
    override val viewModel by viewModel<EditPlaylistViewModel>()
    private val playlistId by lazy { requireArguments().getInt(KEY) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.newPlaylistTb.title = getString(R.string.edit_playlist)
        binding.newPlaylistBtn.text = getString(R.string.save)

        viewModel.loadPlaylist(playlistId)

        viewModel.playlist.observe(viewLifecycleOwner) {playlist ->
            binding.newPlaylistNameEt.setText(playlist.playlistName)
            binding.newPlaylistDescriptionEt.setText(playlist.playlistDescription)

            playlist.imagePath?.toUri()?.let {
                binding.newPlaylistIv.setImageURI(it)
            }
        }

        binding.newPlaylistTb.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    findNavController().navigateUp()
                }
            }
        )

    }

    companion object {
        private const val KEY = PlaylistsFragment.KEY
    }
}