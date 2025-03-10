package com.example.playlistmaker

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.App.Companion.PLAYLISTMAKER_PREF
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView

class SettingsActivity : AppCompatActivity() {
    companion object {
        const val SWITCHER_STATE = "switcherState"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
//
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val share = findViewById<MaterialTextView>(R.id.button_share)
        val getSupport = findViewById<MaterialTextView>(R.id.button_get_support)
        val userAgreement = findViewById<MaterialTextView>(R.id.button_user_agreement)
        val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitcher)

        val sharedPref = getSharedPreferences(PLAYLISTMAKER_PREF, MODE_PRIVATE)

        val isDarkMode = when (AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.MODE_NIGHT_YES -> true
            AppCompatDelegate.MODE_NIGHT_NO -> false
            else -> (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        }
        themeSwitcher.isChecked = sharedPref.getBoolean(SWITCHER_STATE, isDarkMode)

        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            (applicationContext as App).switchTheme(checked)
            sharedPref.edit()
                .putBoolean(SWITCHER_STATE, themeSwitcher.isChecked)
                .apply()
        }

        toolbar.setNavigationOnClickListener {
            finish()
        }

        share.setOnClickListener{
            val message = getString(R.string.course_link)
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.setType("text/plain")
            shareIntent.putExtra(Intent.EXTRA_TEXT, message)
            startActivity(Intent.createChooser(shareIntent, null))
        }

        getSupport.setOnClickListener{
            val email = getString(R.string.email)
            val subject = getString(R.string.email_theme)
            val body = getString(R.string.email_body)
            val emailIntent = Intent(Intent.ACTION_SENDTO)
            emailIntent.data = Uri.parse("mailto:")
            emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
            emailIntent.putExtra(Intent.EXTRA_TEXT, body)
            startActivity(emailIntent)
        }

        userAgreement.setOnClickListener{
            val link = getString(R.string.practicum_offer)
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
            startActivity(browserIntent)
        }


    }
}