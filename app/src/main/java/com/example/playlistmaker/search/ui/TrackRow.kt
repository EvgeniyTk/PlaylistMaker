package com.example.playlistmaker.search.ui

import androidx.annotation.DimenRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.theme.AppTheme
import com.example.playlistmaker.theme.LocalAppExtraColors
import com.example.playlistmaker.util.formatMillis

private val YSDisplayRegular = FontFamily(Font(R.font.ys_display_regular, weight = FontWeight.Normal))
@Composable
fun TrackRow(
    track: Track,
    onClick: () -> Unit
) {
    val colors = LocalAppExtraColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(
                horizontal = dimenDp(R.dimen.padding_editText_horizontal),
                vertical = dimenDp(R.dimen.padding_editText_vertical)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = track.artworkUrl100,
            placeholder = painterResource(R.drawable.track_placeholder),
            error = painterResource(R.drawable.track_placeholder),
            contentDescription = null,
            modifier = Modifier.size(dimenDp(R.dimen.size_track_image)),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = dimenDp(R.dimen.padding_icon)),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = track.trackName,
                color = MaterialTheme.colorScheme.onSecondary,
                fontFamily = YSDisplayRegular,
                fontSize = dimenSp(R.dimen.secondary_text_size),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                lineHeight = dimenSp(R.dimen.secondary_text_size)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = track.artistName,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = YSDisplayRegular,
                    fontSize = dimenSp(R.dimen.third_text_size),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = dimenSp(R.dimen.third_text_size)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Image(
                    painter = painterResource(R.drawable.dot),
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = formatMillis(track.trackTimeMillis),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = YSDisplayRegular,
                    fontSize = dimenSp(R.dimen.third_text_size),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = dimenSp(R.dimen.third_text_size)
                )
            }
        }

        Image(
            painter = painterResource(R.drawable.right_arrow),
            contentDescription = null,
            modifier = Modifier.padding(horizontal = dimenDp(R.dimen.padding_icon))
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TrackRowPreview() {
    val sampleTrack = Track(
        trackName = "Song Title",
        artistName = "Artist Name",
        trackTimeMillis = 215000,
        artworkUrl100 = "drawable://${R.drawable.track_placeholder}",
        trackId = 1,
        collectionName = "1",
        releaseDate = "1990",
        primaryGenreName = "TODO()",
        country = "TODO()",
        isFavorite = true,
        previewUrl = "TODO()"
    )
    AppTheme {
        TrackRow(track = sampleTrack, onClick = {})
    }
}

@Composable
private fun dimenDp(@DimenRes id: Int) = dimensionResource(id)

@Composable
private fun dimenSp(@DimenRes id: Int) = dimenDp(id).value.sp