package com.example.playlistmaker

import android.app.Application
import com.example.playlistmaker.domain.api.SettingsInteractor

class App : Application() {

    companion object {
        const val PLAYLISTMAKER_PREF = "switchTheme"
        const val NIGHT_MODE = "themeMode"
    }

    private lateinit var getSettingsInteractor: SettingsInteractor

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
