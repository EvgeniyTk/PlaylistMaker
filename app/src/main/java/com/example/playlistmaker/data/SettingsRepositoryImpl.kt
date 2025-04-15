package com.example.playlistmaker.data

import android.content.Context
import android.content.res.Configuration.UI_MODE_NIGHT_MASK
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.App.Companion.NIGHT_MODE
import com.example.playlistmaker.Creator
import com.example.playlistmaker.domain.api.SettingsRepository

class SettingsRepositoryImpl(context: Context): SettingsRepository {

    private val sharedPref = Creator.provideSharedPreferences()
    private val resources = context.resources

    private fun isSystemDarkTheme(): Boolean {
        return (resources.configuration.uiMode and UI_MODE_NIGHT_MASK) == UI_MODE_NIGHT_YES
    }

    override fun DarkThemeIsEnabled(): Boolean {
        return sharedPref.getBoolean(NIGHT_MODE, isSystemDarkTheme())
    }

    override fun setDarkTheme(enabled: Boolean) {
        sharedPref.edit().putBoolean(NIGHT_MODE, enabled).apply()
    }

    override fun applyDarkTheme(darkThemeIsEnabled: Boolean) {
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeIsEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

}