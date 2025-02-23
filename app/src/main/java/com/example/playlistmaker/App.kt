package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    private var darkTheme = false
    companion object {
        const val PLAYLISTMAKER_PREF = "switchTheme"
        const val NIGHT_MODE = "themeMode"
    }

    override fun onCreate() {
        super.onCreate()
        val sharedPrefs = getSharedPreferences(PLAYLISTMAKER_PREF, MODE_PRIVATE)
        darkTheme = sharedPrefs.getBoolean(NIGHT_MODE, false)
        switchTheme(darkTheme)

    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        val sharedPrefs = getSharedPreferences(PLAYLISTMAKER_PREF, MODE_PRIVATE)
        sharedPrefs.edit()
            .putBoolean(NIGHT_MODE, darkTheme)
            .apply()

    }

}
