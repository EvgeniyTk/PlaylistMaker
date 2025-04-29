package com.example.playlistmaker.util

import android.app.Application
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.search.view_model.SearchViewModel

class App : Application() {

    companion object {
        const val PLAYLISTMAKER_PREF = "switchTheme"
        const val NIGHT_MODE = "themeMode"
    }

    private lateinit var getSettingsInteractor: SettingsInteractor
    var searchViewModel: SearchViewModel? = null


    override fun onCreate() {

        super.onCreate()

        Creator.initApplication(this)

        getSettingsInteractor = Creator.provideSettingsInteractor(this)

        getSettingsInteractor.darkThemeIsEnabled(
            object : SettingsInteractor.DarkThemeConsumer {
                override fun consume(darkThemeIsEnabled: Boolean) {
                    getSettingsInteractor.applyDarkTheme(darkThemeIsEnabled)
                }

            }
        )

    }
}
