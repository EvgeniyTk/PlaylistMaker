package com.example.playlistmaker.creator

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.playlistmaker.player.data.PlayerRepositoryImpl
import com.example.playlistmaker.player.domain.PlayerInteractor
import com.example.playlistmaker.player.domain.PlayerRepository
import com.example.playlistmaker.player.domain.impl.PlayerInteractorImpl
import com.example.playlistmaker.util.App.Companion.PLAYLISTMAKER_PREF
import com.example.playlistmaker.search.data.SearchHistoryRepositoryImpl
import com.example.playlistmaker.settings.data.SettingsRepositoryImpl
import com.example.playlistmaker.search.data.TracksRepositoryImpl
import com.example.playlistmaker.search.data.network.ITunesApi
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.domain.api.SearchHistoryInteractor
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.api.SettingsRepository
import com.example.playlistmaker.search.domain.api.TracksInteractor
import com.example.playlistmaker.search.domain.api.TracksRepository
import com.example.playlistmaker.search.domain.impl.SearchHistoryInteractorImpl
import com.example.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.example.playlistmaker.search.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.sharing.data.AppLinkProviderImpl
import com.example.playlistmaker.sharing.data.ExternalNavigatorImpl
import com.example.playlistmaker.sharing.domain.api.SharingInteractor
import com.example.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import com.google.gson.Gson
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Creator {
    private val gson by lazy { Gson() }

    private lateinit var application: Application

    fun initApplication(application: Application) {
        Creator.application = application
    }

    private fun provideITunesService(): ITunesApi {
        val iTunesBaseUrl = "https://itunes.apple.com"
        val retrofit = Retrofit.Builder()
            .baseUrl(iTunesBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(ITunesApi::class.java)
    }

    private fun getTrackRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient(provideITunesService(), application))
    }

    private fun getSearchHistoryRepository(): SearchHistoryRepository {
        return SearchHistoryRepositoryImpl(provideSharedPreferences(), gson)
    }

    private fun getSettingsRepository(): SettingsRepository {
        return SettingsRepositoryImpl(provideSharedPreferences(), application.resources)
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTrackRepository())
    }

    fun provideSearchHistoryInteractor(): SearchHistoryInteractor {
        return SearchHistoryInteractorImpl(getSearchHistoryRepository())
    }

    fun provideSettingsInteractor(): SettingsInteractor {
        return SettingsInteractorImpl(getSettingsRepository())
    }

    fun provideSharingInteractor(): SharingInteractor {
        val externalNavigator = ExternalNavigatorImpl(application)
        val appLinkProvider = AppLinkProviderImpl(application)
        return SharingInteractorImpl(externalNavigator, appLinkProvider)
    }

    private fun provideSharedPreferences(): SharedPreferences {
        return application.getSharedPreferences(PLAYLISTMAKER_PREF, Context.MODE_PRIVATE)
    }


    private fun getPlayerRepository(): PlayerRepository = PlayerRepositoryImpl()

    fun providePlayerInteractor(): PlayerInteractor {
         return PlayerInteractorImpl(getPlayerRepository())
    }

}