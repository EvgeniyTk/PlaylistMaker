package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textview.MaterialTextView

class SettingsActivity : AppCompatActivity() {
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
        val toolbar1 = findViewById<Toolbar>(R.id.toolbar)
        val share = findViewById<MaterialTextView>(R.id.button_share)
        val getSupport = findViewById<MaterialTextView>(R.id.button_get_support)
        val userAgreement = findViewById<MaterialTextView>(R.id.button_user_agreement)


        toolbar1.setNavigationOnClickListener {
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