package com.example.playlistmaker.player.ui


import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlayerBinding
import com.example.playlistmaker.library.domain.model.Playlist
import com.example.playlistmaker.player.model.AddTrackStatus
import com.example.playlistmaker.player.model.PlayerState
import com.example.playlistmaker.player.view_model.PlayerViewModel
import com.example.playlistmaker.search.domain.models.Track
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerFragment : Fragment() {
    private val viewModel by viewModel<PlayerViewModel>()

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: PlaylistInPlayerAdapter

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

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        adapter = PlaylistInPlayerAdapter { viewModel.addTrackToPlaylist(it) }
        binding.playerRv.layoutManager = LinearLayoutManager(requireContext())
        binding.playerRv.adapter = adapter

        val bottomSheetContainer = binding.standardBottomSheet
        val scrim = binding.bottomSheetScrim
        val bottomSheetBehavior =  BottomSheetBehavior.from(bottomSheetContainer).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.playlistButton.setOnClickListener {
            viewModel.onPlaylistButtonClicked()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        scrim.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.addPlaylistInPlayerButton.setOnClickListener{
            findNavController().navigate(R.id.action_playerFragment_to_newPlaylistFragment)
        }

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    scrim.isVisible = false
                } else {
                    scrim.isVisible = true
                }

            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                scrim.isVisible = true
                scrim.alpha = slideOffset + 0.5f
            }

        })


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

        viewModel.isFavorite.observe(viewLifecycleOwner) {
            if (it) {
                binding.favouritesButton.setImageResource(R.drawable.favorite_true)
            } else {
                binding.favouritesButton.setImageResource(R.drawable.favorite_false)
            }
        }

        viewModel.playlists.observe(viewLifecycleOwner){
            showPlaylists(it)
        }

        viewModel.addTrackStatus.observe(viewLifecycleOwner) { status ->
            when(status) {
                is AddTrackStatus.Success -> {
                    Toast.makeText(requireContext(),
                        getString(R.string.added_track_in_playlist, status.playlistName), Toast.LENGTH_SHORT)
                        .show()
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                }
                is AddTrackStatus.AlreadyAdded -> {
                    Toast.makeText(context,
                        getString(R.string.track_already_added, status.playlistName), Toast.LENGTH_SHORT).show()
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

        binding.favouritesButton.setOnClickListener{
            viewModel.onFavoriteClicked()
        }


    }

    private fun showPlaylists(list: List<Playlist>) {
        adapter.updateData(list)
        binding.playerRv.isVisible = list.isNotEmpty()
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
            binding.infoGroup.isVisible = false
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