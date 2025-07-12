package com.example.playlistmaker.di


import com.example.playlistmaker.library.data.FavoritesRepositoryImpl
import com.example.playlistmaker.db.data.converters.TrackDbConvertor
import com.example.playlistmaker.library.domain.FavoritesRepository
import com.example.playlistmaker.player.data.PlayerRepositoryImpl
import com.example.playlistmaker.player.domain.PlayerRepository
import com.example.playlistmaker.search.data.TracksRepositoryImpl
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.domain.api.ExternalNavigator
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val repositoryModule = module {
    factory<TracksRepository> {
        TracksRepositoryImpl(get(), get())
    }

    factory<SettingsRepository> {
        SettingsRepositoryImpl(get(), get())
    }

    factory<ExternalNavigator> {
        ExternalNavigatorImpl(androidContext())
    }

    factory<PlayerRepository> {
        PlayerRepositoryImpl(get())
    }

    factory { TrackDbConvertor() }

    factory<FavoritesRepository> {
        FavoritesRepositoryImpl(get(), get())
    }
}

