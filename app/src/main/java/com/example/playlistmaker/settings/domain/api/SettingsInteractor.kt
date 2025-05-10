package com.example.playlistmaker.settings.domain.api

interface SettingsInteractor {
    fun darkThemeIsEnabled(): Boolean
    fun setDarkTheme(enabled: Boolean)
    fun applyDarkTheme(darkThemeIsEnabled: Boolean)

}