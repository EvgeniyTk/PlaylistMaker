package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.SettingsInteractor
import com.example.playlistmaker.domain.api.SettingsRepository

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