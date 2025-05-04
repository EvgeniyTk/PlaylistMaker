package com.example.playlistmaker.util

import android.app.Application
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.settings.domain.api.SettingsInteractor

class App : Application() {

    companion object {
        const val PLAYLISTMAKER_PREF = "switchTheme"
        const val NIGHT_MODE = "themeMode"
    }

    override fun onCreate() {

        super.onCreate()

        Creator.initApplication(this)

        val getSettingsInteractor = Creator.provideSettingsInteractor()

        getSettingsInteractor.darkThemeIsEnabled(
            object : SettingsInteractor.DarkThemeConsumer {
                override fun consume(darkThemeIsEnabled: Boolean) {
                    getSettingsInteractor.applyDarkTheme(darkThemeIsEnabled)
                }

            }
        )

    }
}
