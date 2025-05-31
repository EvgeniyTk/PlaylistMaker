package com.example.playlistmaker.player.ui

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.player.model.PlayerState
import com.example.playlistmaker.player.view_model.PlayerViewModel
import com.example.playlistmaker.search.domain.models.Track
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerFragment : Fragment() {
    private val viewModel by viewModel<PlayerViewModel>()

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel.playerUiState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PlayerState.Prepared -> {
                    binding.playButton.isEnabled = true
                    binding.timePlay.text = state.initialTime
                }

                is PlayerState.Playing -> {
                    binding.playButton.setImageResource(R.drawable.pause_button)
                }

                is PlayerState.Paused -> {
                    binding.playButton.setImageResource(R.drawable.play_button)
                }

                is PlayerState.TimeUpdated -> {
                    binding.timePlay.text = state.time
                }
            }
        }

        val track: Track? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireArguments().getParcelable(ARGS_TRACK, Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            requireArguments().getParcelable(ARGS_TRACK)
        }

        if (track != null) {
            bindTrack(track)
            viewModel.setTrack(track)
            viewModel.setUrl(track.previewUrl)
        }

        binding.playButton.setOnClickListener {
            viewModel.playbackControl()
        }

        binding.toolbarPlayer.setNavigationOnClickListener {
            findNavController().navigateUp()
        }


    }

    private fun bindTrack(track: Track) {
        Glide.with(requireContext())
            .load(track.getCoverArtwork())
            .transform(CenterCrop(), RoundedCorners(16))
            .placeholder(R.drawable.track_placeholder)
            .into(binding.trackImage)

        binding.trackName.text = track.trackName
        binding.artistName.text = track.artistName
        binding.trackDurationValue.text = SimpleDateFormat("mm:ss", Locale.getDefault())
            .format(track.trackTimeMillis)

        if (track.collectionName.isEmpty()) {
            binding.infoGroup.visibility = View.GONE
        } else {
            binding.collectionValue.text = track.collectionName
        }

        binding.releaseDateValue.text = track.releaseDate.substring(0, 4)
        binding.genreValue.text = track.primaryGenreName
        binding.countryValue.text = track.country
    }

    override fun onPause() {
        super.onPause()
        viewModel.pauseIfNeeded()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARGS_TRACK = "track"
        fun createArgs(track: Track): Bundle = bundleOf(ARGS_TRACK to track)

    }
}