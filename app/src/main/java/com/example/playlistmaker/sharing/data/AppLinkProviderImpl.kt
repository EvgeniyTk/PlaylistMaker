package com.example.playlistmaker.sharing.data

import android.content.Context
import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.domain.api.AppLinkProvider
import com.example.playlistmaker.sharing.domain.model.EmailData

class AppLinkProviderImpl(private val context: Context) : AppLinkProvider {

    override fun getShareAppLink(): String {
        return context.getString(R.string.course_link)
    }

    override fun getTermsLink(): String {
        return context.getString(R.string.practicum_offer)
    }

    override fun getSupportEmailData(): EmailData {
        return EmailData(
            email = context.getString(R.string.email),
            subject = context.getString(R.string.email_theme),
            body = context.getString(R.string.email_body)
        )
    }
}