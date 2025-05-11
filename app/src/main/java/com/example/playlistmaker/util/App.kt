package com.example.playlistmaker.util

import android.app.Application
import com.example.playlistmaker.di.dataModule
import com.example.playlistmaker.di.interactorModule
import com.example.playlistmaker.di.repositoryModule
import com.example.playlistmaker.di.viewModelModule
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import org.koin.android.ext.android.getKoin
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class App : Application() {

    companion object {
        const val PLAYLISTMAKER_PREF = "switchTheme"
        const val NIGHT_MODE = "themeMode"
    }

    override fun onCreate() {

        super.onCreate()
        startKoin {
            printLogger(Level.DEBUG)
            androidContext(this@App)
            modules(dataModule, interactorModule, repositoryModule, viewModelModule)
        }


        val getSettingsInteractor = getKoin().get<SettingsInteractor>()

        getSettingsInteractor.applyDarkTheme(getSettingsInteractor.darkThemeIsEnabled())

    }
}
