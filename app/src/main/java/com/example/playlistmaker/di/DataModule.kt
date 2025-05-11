package com.example.playlistmaker.di

import android.content.Context
import android.media.MediaPlayer
import com.example.playlistmaker.search.data.NetworkClient
import com.example.playlistmaker.search.data.SearchHistoryRepositoryImpl
import com.example.playlistmaker.search.data.network.ITunesApi
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.search.domain.api.SearchHistoryRepository
import com.example.playlistmaker.sharing.data.AppLinkProviderImpl
import com.example.playlistmaker.sharing.domain.api.AppLinkProvider
import com.example.playlistmaker.util.App.Companion.PLAYLISTMAKER_PREF
import com.google.gson.Gson
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {
    single<ITunesApi> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ITunesApi::class.java)
    }

    single {
        androidContext().getSharedPreferences(PLAYLISTMAKER_PREF, Context.MODE_PRIVATE)
    }

    single {
        androidContext().resources
    }

    factory { Gson() }

    factory<SearchHistoryRepository> {
        SearchHistoryRepositoryImpl(get(),get())
    }

    single<NetworkClient>{
        RetrofitNetworkClient(get(), androidContext())
    }

    single<AppLinkProvider> {
        AppLinkProviderImpl(androidContext())
    }

    factory {
        MediaPlayer()
    }

}