package com.example.playlistmaker.library.ui

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.setPadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.library.domain.model.Playlist
import com.example.playlistmaker.library.ui.models.PlaylistScreenState
import com.example.playlistmaker.library.view_model.PlaylistViewModel
import com.example.playlistmaker.main.ui.RootActivity
import com.example.playlistmaker.player.ui.PlayerFragment
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.util.debounce
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class PlaylistFragment : Fragment() {
    private val viewModel: PlaylistViewModel by viewModel()
    private val playlistId by lazy { requireArguments().getInt(KEY) }
    private var _binding: FragmentPlaylistBinding? = null
    private lateinit var adapter: TracksInPlaylistAdapter
    private lateinit var onTrackClickDebounce: (Track) -> Unit
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadPlaylist(playlistId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(binding.playlist) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
        val behavior = BottomSheetBehavior.from(binding.playlistFragmentBottomSheet)

        binding.playlistFragmentBottomSheet.post {
            behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        val menuBehavior = BottomSheetBehavior.from(binding.playlistFragmentMenu).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        menuBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    binding.bottomSheetScrim.isVisible = false
                } else {
                    binding.bottomSheetScrim.isVisible = true
                }

            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.bottomSheetScrim.isVisible = true
                binding.bottomSheetScrim.alpha = slideOffset + 0.5f
            }

        })

        binding.bottomSheetScrim.setOnClickListener {
            menuBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }

        binding.playlistFragmentMenuIb.setOnClickListener {
            menuBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        onTrackClickDebounce = debounce<Track>(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            findNavController().navigate(
                R.id.action_playlistFragment_to_playerFragment,
                PlayerFragment.createArgs(track)
            )
        }

        viewModel.openTrackEvent.observe(viewLifecycleOwner) { track ->
            view.post {
                (activity as? RootActivity)?.animateBottomNavigationView()
                onTrackClickDebounce(track)
            }
        }

        adapter = TracksInPlaylistAdapter(
            clickListener = { viewModel.onTrackClick(it) },
            longClickListener = { showDeleteTrackConfirmationDialog(it) }
        )

        binding.playlistFragmentRv.layoutManager = LinearLayoutManager(requireContext())
        binding.playlistFragmentRv.adapter = adapter

        viewModel.loadPlaylist(playlistId)

        val includedView = binding.playlistView

        viewModel.screenState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is PlaylistScreenState.PlaylistContent -> {
                    val playlist = state.playlist
                    val tracks = state.tracks
                    binding.playlistFragmentNameTv.text = playlist.playlistName
                    binding.playlistFragmentDescriptionTv.text = playlist.playlistDescription
                    binding.playlistFragmentCountTracksTv.text =
                        getWordFormTracks(playlist.tracksCount)
                    val durationSum = (tracks.sumOf { it.trackTimeMillis } / 60000).toInt()
                    binding.playlistFragmentTracksDurationTv.text =
                        getWordFormMinutes(durationSum)

                    val imageFile = playlist.imagePath?.let { File(it) }
                    Glide.with(binding.playlistFragmentImageIv.context)
                        .load(if (imageFile?.exists() == true) imageFile else null)
                        .placeholder(R.drawable.track_placeholder)
                        .error(R.drawable.track_placeholder)
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ) = false

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                binding.playlistFragmentImageIv.setPadding(0)
                                return false
                            }

                        })
                        .into(binding.playlistFragmentImageIv)
                    adapter.updateData(tracks)

                    includedView.playlistInPlayerNameTv.text = playlist.playlistName
                    includedView.playlistInPlayerCountTv.text = getWordFormTracks(playlist.tracksCount)
                    Glide.with(includedView.playlistInPlayerIv.context)
                        .load(if (imageFile?.exists() == true) imageFile else null)
                        .placeholder(R.drawable.track_placeholder)
                        .error(R.drawable.track_placeholder)
                        .into(includedView.playlistInPlayerIv)
                }
            }
        }

        viewModel.playlistDeleted.observe(viewLifecycleOwner) {deleted ->
            if (deleted) {
                findNavController().navigateUp()
            }
        }

        binding.playlistToolbar.setNavigationOnClickListener {
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
        binding.playlistFragmentShareIb.setOnClickListener {
            sharePlaylist()
        }
        binding.sharePlaylistBtn.setOnClickListener {
            sharePlaylist()
        }

        binding.editInfoPlaylistBtn.setOnClickListener {

        }

        binding.deletePlaylistBtn.setOnClickListener {
            menuBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            val state = viewModel.screenState.value
            if (state is PlaylistScreenState.PlaylistContent) {
                showDeletePlaylistConfirmationDialog(state.playlist)
            }
        }

    }

    private fun getWordFormTracks(count: Int): String {
        return binding.root.context.resources.getQuantityString(
            R.plurals.track_count,
            count,
            count
        )
    }

    private fun getWordFormMinutes(duration: Int): String {
        return binding.root.context.resources.getQuantityString(
            R.plurals.minutes_count,
            duration,
            duration
        )
    }

    private fun showDeleteTrackConfirmationDialog(track: Track) {
        MaterialAlertDialogBuilder(requireContext(), R.style.CustomAlertDialogTheme)
            .setTitle(getString(R.string.delete_track_question))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                viewModel.deleteTrack(track.trackId, playlistId)
            }
            .setNegativeButton(getString(R.string.no), null)
            .show()
    }

    private fun showDeletePlaylistConfirmationDialog(playlist: Playlist) {
        MaterialAlertDialogBuilder(requireContext(), R.style.CustomAlertDialogTheme)
            .setTitle(getString(R.string.delete_playlist))
            .setMessage(getString(R.string.delete_playlist_question, playlist.playlistName))
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                viewModel.deletePlaylist(playlist.playlistId)
            }
            .setNegativeButton(getString(R.string.no), null)
            .show()
    }

    private fun sharePlaylist() {
        val state = viewModel.screenState.value
        if (state is PlaylistScreenState.PlaylistContent) {
            val playlist = state.playlist
            val tracks = state.tracks
            if (tracks.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.no_tracks_to_share),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val message = buildShareMessage(playlist, tracks)
                viewModel.sharePlaylistMessage(message)
            }
        }
    }

    private fun buildShareMessage(
        playlist: Playlist,
        tracks: List<Track>
    ): String {
        val builder = StringBuilder()
        builder.appendLine(playlist.playlistName)
        builder.appendLine(playlist.playlistDescription)
        builder.appendLine(getWordFormTracks(tracks.size))
        builder.appendLine()

        tracks.forEachIndexed { index, track ->
            val duration = SimpleDateFormat("mm:ss", Locale.getDefault())
                .format(track.trackTimeMillis)
            builder.appendLine("${index + 1}. ${track.artistName} - ${track.trackName} ($duration)")
        }
        return builder.toString()
    }


    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 300L
        private const val KEY = PlaylistsFragment.KEY

    }
}