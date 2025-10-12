package com.example.playlistmaker.settings.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.playlistmaker.settings.view_model.SettingsViewModel
import com.example.playlistmaker.theme.AppTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment: Fragment() {
    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                AppTheme {
                    var isDarkTheme by remember { mutableStateOf(false) }

                    LaunchedEffect(Unit) {
                        viewModel.isDarkTheme.observe(viewLifecycleOwner) { isDark ->
                            isDarkTheme = isDark
                        }
                    }

                    SettingsScreen(
                        isDarkTheme = isDarkTheme,
                        onDarkThemeChanged = { viewModel.setDarkTheme(it) },
                        onShareApp = { viewModel.shareApp() },
                        onGetSupport = { viewModel.openSupport() },
                        onUserAgreement = { viewModel.openTerms() }
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }
    }
}