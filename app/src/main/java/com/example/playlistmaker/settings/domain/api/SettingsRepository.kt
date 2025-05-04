package com.example.playlistmaker.settings.domain.api

interface SettingsRepository {
    fun DarkThemeIsEnabled(): Boolean
    fun setDarkTheme(enabled: Boolean)
    fun applyDarkTheme(darkThemeIsEnabled: Boolean)
}