package com.example.playlistmaker.di

import com.example.playlistmaker.library.view_model.FavoriteTracksViewModel
import com.example.playlistmaker.library.view_model.NewPlaylistViewModel
import com.example.playlistmaker.library.view_model.PlaylistViewModel
import com.example.playlistmaker.library.view_model.PlaylistsViewModel
import com.example.playlistmaker.player.view_model.PlayerViewModel
import com.example.playlistmaker.search.view_model.SearchViewModel
import com.example.playlistmaker.settings.view_model.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        SearchViewModel(get(), get())
    }
    viewModel {
        SettingsViewModel(get(), get())
    }
    viewModel{
        PlayerViewModel(get(), get(), get())
    }
    viewModel{
        FavoriteTracksViewModel(get())
    }
    viewModel{
        PlaylistsViewModel(get())
    }
    viewModel{
        NewPlaylistViewModel(get())
    }
    viewModel{
        PlaylistViewModel(get())
    }
}