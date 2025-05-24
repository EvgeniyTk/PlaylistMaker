package com.example.playlistmaker.library.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentFavoriteTracksBinding
import com.example.playlistmaker.library.ui.models.FavoriteTracksState
import com.example.playlistmaker.library.view_model.FavoriteTracksViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FavoriteTracksFragment: Fragment() {

    companion object{
        fun newInstance(): FavoriteTracksFragment {
            return FavoriteTracksFragment()
        }
    }

    private val viewModel: FavoriteTracksViewModel by viewModel()
    private var _binding: FragmentFavoriteTracksBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFavoriteTracksBinding.inflate(inflater, container, false)

        viewModel.observeState().observe(viewLifecycleOwner) {
            render(it)
        }

        viewModel.setState(FavoriteTracksState.Empty) // заглушка

        return binding.root


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showPlaceholder(){
        binding.favoritesPlaceholderIV.visibility = View.VISIBLE
        binding.favoritesPlaceholderTV.visibility = View.VISIBLE
    }

    private fun render(state: FavoriteTracksState) {
        when (state) {
            FavoriteTracksState.Empty -> showPlaceholder()
        }
    }
}