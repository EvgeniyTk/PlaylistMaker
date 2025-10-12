package com.example.playlistmaker.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.playlistmaker.R


data class AppExtraColors(
    val textColorPrimary: Color,
    val textColorSecondary: Color,
    val colorIconPrimary: Color,
    val colorIconSecondary: Color,
    val cursorColor: Color,
    val editTextBackgroundColor: Color,
    val textColorSearchHint: Color,
    val clearButtonColor: Color,
    val switchThumbColor: Color,
    val switchTrackColor: Color,
    val settingsItemColor: Color
)

val LocalAppExtraColors = staticCompositionLocalOf<AppExtraColors> {
    error("No AppExtraColors provided")
}

val YSDisplayRegular = FontFamily(
    Font(R.font.ys_display_regular, weight = FontWeight.Normal)
)

val YSDisplayMedium = FontFamily(
    Font(R.font.ys_display_medium, weight = FontWeight.Medium)
)


@Composable
private fun appLightColorScheme() = lightColorScheme(
    primary = colorResource(R.color.blue),
    onPrimary = colorResource(R.color.white),
    secondary = colorResource(R.color.white),
    onSecondary = colorResource(R.color.black),
    surface = colorResource(R.color.white),
    onSurface = colorResource(R.color.black_app),
    primaryContainer = colorResource(R.color.blue),
    onPrimaryContainer = colorResource(R.color.white)
)

@Composable
private fun appDarkColorScheme() = darkColorScheme(
    primary = colorResource(R.color.black_app),
    onPrimary = colorResource(R.color.white),
    secondary = colorResource(R.color.black_app),
    onSecondary = colorResource(R.color.white),
    surface = colorResource(R.color.black_app),
    onSurface = colorResource(R.color.white),
    primaryContainer = colorResource(R.color.black_app),
    onPrimaryContainer = colorResource(R.color.white)
)

@Composable
private fun appLightExtraColors() = AppExtraColors(
    textColorPrimary = colorResource(R.color.white),
    textColorSecondary = colorResource(R.color.black_app),
    colorIconPrimary = colorResource(R.color.black_app),
    colorIconSecondary = colorResource(R.color.grey),
    cursorColor = colorResource(R.color.blue),
    editTextBackgroundColor = colorResource(R.color.edittext_background),
    textColorSearchHint = colorResource(R.color.grey),
    clearButtonColor = colorResource(R.color.clear_button_color),
    switchThumbColor = colorResource(R.color.switch_thumb_color),
    switchTrackColor = colorResource(R.color.switch_track_color),
    settingsItemColor = colorResource(R.color.black_app)
)

@Composable
private fun appDarkExtraColors() = AppExtraColors(
    textColorPrimary = colorResource(R.color.white),
    textColorSecondary = colorResource(R.color.white),
    colorIconPrimary = colorResource(R.color.white),
    colorIconSecondary = colorResource(R.color.white),
    cursorColor = colorResource(R.color.blue),
    editTextBackgroundColor = colorResource(R.color.white),
    textColorSearchHint = colorResource(R.color.black_app),
    clearButtonColor = colorResource(R.color.black_app),
    switchThumbColor = colorResource(R.color.switch_thumb_color),
    switchTrackColor = colorResource(R.color.switch_track_color),
    settingsItemColor = colorResource(R.color.white)
)

@Composable
fun AppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (useDarkTheme) appDarkColorScheme() else appLightColorScheme()
    val extra = if (useDarkTheme) appDarkExtraColors() else appLightExtraColors()

    CompositionLocalProvider(LocalAppExtraColors provides extra) {
        MaterialTheme(
            colorScheme = colors,
            typography = MaterialTheme.typography,
            content = content
        )
    }
}