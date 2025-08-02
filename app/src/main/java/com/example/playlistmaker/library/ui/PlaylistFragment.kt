package com.example.playlistmaker.library.ui

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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
import com.example.playlistmaker.library.ui.PlaylistsFragment.Companion.KEY
import com.example.playlistmaker.library.ui.models.PlaylistScreenState
import com.example.playlistmaker.library.view_model.PlaylistViewModel
import com.example.playlistmaker.main.ui.RootActivity
import com.example.playlistmaker.player.ui.PlayerFragment
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.TrackAdapter
import com.example.playlistmaker.util.debounce
import com.google.android.material.bottomsheet.BottomSheetBehavior
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class PlaylistFragment : Fragment() {
    private val viewModel: PlaylistViewModel by viewModel()
    private var _binding: FragmentPlaylistBinding? = null
    private lateinit var adapter: TrackAdapter
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

        adapter = TrackAdapter { viewModel.onTrackClick(it) }
        binding.playlistFragmentRv.layoutManager = LinearLayoutManager(requireContext())
        binding.playlistFragmentRv.adapter = adapter


        val playlistId = requireArguments().getInt(KEY)
        viewModel.loadPlaylist(playlistId)

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
                }
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

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 300L
    }
}