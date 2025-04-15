package com.example.playlistmaker.data

import android.content.SharedPreferences
import android.content.res.Configuration.UI_MODE_NIGHT_MASK
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.content.res.Resources
import androidx.appcompat.app.AppCompatDelegate
import com.example.playlistmaker.App.Companion.NIGHT_MODE
import com.example.playlistmaker.domain.api.SettingsRepository

class SettingsRepositoryImpl(private val sharedPref: SharedPreferences, private val resources: Resources): SettingsRepository {

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
        if (!sharedPref.contains(NIGHT_MODE)){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        } else {
            AppCompatDelegate.setDefaultNightMode(
                if (darkThemeIsEnabled) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            )
        }

    }

}