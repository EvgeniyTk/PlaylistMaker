package com.example.playlistmaker.library.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.library.domain.model.Playlist
import com.example.playlistmaker.theme.AppTheme
import com.example.playlistmaker.theme.LocalAppExtraColors
import com.example.playlistmaker.theme.YSDisplayRegular

@Composable
fun PlaylistItem(
    playlist: Playlist,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Card(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            AsyncImage(
                model = playlist.imagePath,
                contentDescription = null,
                placeholder = painterResource(id = R.drawable.track_placeholder),
                error = painterResource(id = R.drawable.track_placeholder),
                fallback = painterResource(id = R.drawable.track_placeholder),
                modifier = Modifier
                    .fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Text(
            text = playlist.playlistName,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            maxLines = 1,
            fontFamily = YSDisplayRegular,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            color = LocalAppExtraColors.current.textColorSecondary,
            overflow = TextOverflow.Ellipsis,
            lineHeight = dimensionResource(R.dimen.secondary_text_size).value.sp
        )

        Text(
            text = pluralStringResource(
                id = R.plurals.track_count,
                count = playlist.tracksCount,
                playlist.tracksCount
            ),
            modifier = Modifier.fillMaxWidth(),
            maxLines = 1,
            fontFamily = YSDisplayRegular,
            fontWeight = FontWeight.Normal,
            fontSize = 12.sp,
            color = LocalAppExtraColors.current.textColorSecondary,
            overflow = TextOverflow.Ellipsis,
            lineHeight = dimensionResource(R.dimen.third_text_size).value.sp
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun PlaylistItemPreview() {
    AppTheme {
        PlaylistItem(
            playlist = Playlist(
                playlistId = 1,
                playlistName = "Best songs",
                playlistDescription = "My favorite tracks",
                imagePath = "drawable://${R.drawable.track_placeholder}",
                trackIdList = emptyList(),
                tracksCount = 1
            )
        )
    }
}