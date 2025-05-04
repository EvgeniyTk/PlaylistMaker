package com.example.playlistmaker.settings.domain.impl

import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.settings.domain.api.SettingsRepository

class SettingsInteractorImpl(private val repository: SettingsRepository): SettingsInteractor {
    override fun darkThemeIsEnabled(consumer: SettingsInteractor.DarkThemeConsumer) {
        consumer.consume(repository.DarkThemeIsEnabled())
    }

    override fun setDarkTheme(enabled: Boolean) {
        repository.setDarkTheme(enabled)
    }

    override fun applyDarkTheme(darkThemeIsEnabled: Boolean) {
        repository.applyDarkTheme(darkThemeIsEnabled)
    }
}