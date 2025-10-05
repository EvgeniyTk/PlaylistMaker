package com.example.playlistmaker.settings.ui

import androidx.annotation.DimenRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.theme.AppTheme
import com.example.playlistmaker.theme.LocalAppExtraColors
import com.google.android.material.switchmaterial.SwitchMaterial

private val YSDisplayRegular = FontFamily(Font(R.font.ys_display_regular, weight = FontWeight.Normal))
private val YSDisplayMedium = FontFamily(Font(R.font.ys_display_medium, weight = FontWeight.Medium))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    isDarkTheme: Boolean,
    onDarkThemeChanged: (Boolean) -> Unit,
    onShareApp: () -> Unit,
    onGetSupport: () -> Unit,
    onUserAgreement: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.secondary)
    ) {
        SettingsToolbar()

        ThemeSwitch(
            isDarkTheme = isDarkTheme,
            onThemeChanged = onDarkThemeChanged
        )

        SettingsItem(
            text = stringResource(R.string.share_app),
            icon = R.drawable.share,
            onClick = onShareApp
        )

        SettingsItem(
            text = stringResource(R.string.get_support),
            icon = R.drawable.support,
            onClick = onGetSupport
        )

        SettingsItem(
            text = stringResource(R.string.user_agreement),
            icon = R.drawable.right_arrow,
            onClick = onUserAgreement
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsToolbar() {
    TopAppBar(
        title = {
            Box(

            ) {
                Text(
                    text = stringResource(R.string.settings),
                    fontSize = dimenSp(R.dimen.primary_text_size),
                    fontFamily = YSDisplayMedium,
                    fontWeight = FontWeight.Medium,
                    color = LocalAppExtraColors.current.textColorSecondary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            titleContentColor = LocalAppExtraColors.current.textColorSecondary
        ),
        modifier = Modifier.heightIn(dimenDp(R.dimen.height_toolbar)).padding(bottom = 12.dp)
    )
}

@Composable
private fun ThemeSwitch(
    isDarkTheme: Boolean,
    onThemeChanged: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = dimenDp(R.dimen.padding_from_edges),
                vertical = dimenDp(R.dimen.padding_view_settings)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.dark_theme),
            fontSize = dimenSp(R.dimen.secondary_text_size),
            fontFamily = YSDisplayRegular,
            fontWeight = FontWeight.Normal,
            color = LocalAppExtraColors.current.textColorSecondary,
            modifier = Modifier.weight(1f)
        )
        CustomSwitch2(
            checked = isDarkTheme,
            onCheckedChange = onThemeChanged
            )
    }
}

@Composable
fun CustomSwitch2(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val trackWidth = 32.dp
    val trackHeight = 12.dp
    val thumbDiameter = 18.dp

    Box(
        modifier = Modifier
            .size(width = trackWidth, height = thumbDiameter) // Увеличиваем высоту контейнера
            .clickable { onCheckedChange(!checked) }
    ) {
        // Трек (фон)
        Box(
            modifier = Modifier
                .size(width = trackWidth, height = trackHeight)
                .align(Alignment.Center)
                .background(
                    color = if (checked) colorResource(R.color.switch_thumb_active_color) else colorResource(R.color.switch_thumb_inactive_color),
                    shape = RoundedCornerShape(percent = 50)
                )
        )
        // Кружок (thumb)
        Box(
            modifier = Modifier
                .size(thumbDiameter)
                .align(Alignment.CenterStart)
                .offset(
                    x = if (checked) (trackWidth - thumbDiameter) else 0.dp
                )
                .background(
                    color = if (checked) colorResource(R.color.switch_track_active_color) else colorResource(R.color.switch_track_inactive_color),
                    shape = CircleShape
                )
        )
    }
}



@Composable
private fun SettingsItem(
    text: String,
    icon: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(
                horizontal = dimenDp(R.dimen.padding_from_edges),
                vertical = dimenDp(R.dimen.padding_view_settings)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            fontSize = dimenSp(R.dimen.secondary_text_size),
            fontFamily = YSDisplayRegular,
            color = LocalAppExtraColors.current.settingsItemColor,
            modifier = Modifier.weight(1f)
        )

        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = LocalAppExtraColors.current.colorIconSecondary,
            modifier = Modifier.size(24.dp)
                .padding(3.dp)
        )
    }
}

@Composable
private fun dimenDp(@DimenRes id: Int) = dimensionResource(id)

@Composable
private fun dimenSp(@DimenRes id: Int) = dimenDp(id).value.sp

@Preview(showBackground = true)
@Composable
fun SettingsScreenLightPreview() {
    AppTheme(useDarkTheme = false) {
        SettingsScreen(
            isDarkTheme = false,
            onDarkThemeChanged = {},
            onShareApp = {},
            onGetSupport = {},
            onUserAgreement = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenDarkPreview() {
    AppTheme(useDarkTheme = true) {
        SettingsScreen(
            isDarkTheme = true,
            onDarkThemeChanged = {},
            onShareApp = {},
            onGetSupport = {},
            onUserAgreement = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsToolbarPreview() {
    AppTheme {
        SettingsToolbar()
    }
}

@Preview(showBackground = true)
@Composable
fun ThemeSwitchPreview() {
    AppTheme {
        Column {
            ThemeSwitch(
                isDarkTheme = false,
                onThemeChanged = {}
            )
            ThemeSwitch(
                isDarkTheme = true,
                onThemeChanged = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsItemPreview() {
    AppTheme {
        Column {
            SettingsItem(
                text = "Share App",
                icon = R.drawable.share,
                onClick = {}
            )
            SettingsItem(
                text = "Get Support",
                icon = R.drawable.support,
                onClick = {}
            )
            SettingsItem(
                text = "User Agreement",
                icon = R.drawable.right_arrow,
                onClick = {}
            )
        }
    }
}