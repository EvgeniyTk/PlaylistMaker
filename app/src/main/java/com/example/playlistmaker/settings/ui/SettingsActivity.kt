package com.example.playlistmaker.settings.ui


import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.creator.Creator
import com.example.playlistmaker.databinding.ActivitySettingsBinding
import com.example.playlistmaker.settings.view_model.SettingsViewModel


class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var viewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.settings) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel = ViewModelProvider(
            this,
            SettingsViewModel.getViewModelFactory(
                Creator.provideSettingsInteractor(this),
                Creator.provideSharingInteractor(this) // добавляем SharingInteractor
            )
        )[SettingsViewModel::class.java]

        viewModel.isDarkTheme.observe(this) { isDarkTheme ->
            binding.themeSwitcher.isChecked = isDarkTheme
        }

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.themeSwitcher.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setDarkTheme(isChecked)
        }

        binding.buttonShare.setOnClickListener {
            viewModel.shareApp()
        }

        binding.buttonGetSupport.setOnClickListener {
            viewModel.openSupport()
        }

        binding.buttonUserAgreement.setOnClickListener {
            viewModel.openTerms()
        }
    }
}