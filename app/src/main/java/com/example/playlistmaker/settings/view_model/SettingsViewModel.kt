package com.example.playlistmaker.settings.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.playlistmaker.settings.domain.api.SettingsInteractor
import com.example.playlistmaker.sharing.domain.api.SharingInteractor

class SettingsViewModel(
    private val settingsInteractor: SettingsInteractor,
    private val sharingInteractor: SharingInteractor
) : ViewModel() {

    private val _isDarkTheme = MutableLiveData<Boolean>()
    val isDarkTheme: LiveData<Boolean> = _isDarkTheme

    init {
        settingsInteractor.darkThemeIsEnabled(object : SettingsInteractor.DarkThemeConsumer {
            override fun consume(darkThemeIsEnabled: Boolean) {
                _isDarkTheme.postValue(darkThemeIsEnabled)
            }
        })
    }

    fun setDarkTheme(enabled: Boolean) {
        settingsInteractor.setDarkTheme(enabled)
        settingsInteractor.applyDarkTheme(enabled)
    }

    fun shareApp() {
        sharingInteractor.shareApp()
    }

    fun openSupport() {
        sharingInteractor.openSupport()
    }

    fun openTerms() {
        sharingInteractor.openTerms()
    }

    companion object {
        fun getViewModelFactory(
            settingsInteractor: SettingsInteractor,
            sharingInteractor: SharingInteractor
        ): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SettingsViewModel(settingsInteractor, sharingInteractor)
            }
        }
    }
}



