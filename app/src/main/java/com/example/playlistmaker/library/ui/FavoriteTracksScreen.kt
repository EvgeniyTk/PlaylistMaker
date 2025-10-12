import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.library.ui.models.FavoriteTracksState
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.TrackRow
import com.example.playlistmaker.theme.AppTheme
import com.example.playlistmaker.theme.YSDisplayMedium

@Composable
fun FavoriteTracksScreen(
    state: FavoriteTracksState,
    onTrackClick: (Track) -> Unit
) {
    when (state) {
        FavoriteTracksState.Empty -> EmptyPlaceholder()
        is FavoriteTracksState.Content -> TracksList(
            tracks = state.tracks,
            onTrackClick = onTrackClick
        )
    }
}

@Composable
private fun EmptyPlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 106.dp),
        horizontalAlignment = Alignment.CenterHorizontally,

        ) {

        Image(
            painter = painterResource(id = R.drawable.no_result),
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.margin_button)))

        Text(
            text = stringResource(id = R.string.media_library_is_empty),
            fontFamily = YSDisplayMedium,
            fontWeight = FontWeight.Medium,
            fontSize = dimensionResource(R.dimen.placeholder_text_size).value.sp,
            color = MaterialTheme.colorScheme.onSecondary
        )
    }
}

@Composable
private fun TracksList(
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = dimensionResource(id = R.dimen.margin_track))
    ) {
        items(
            items = tracks,
            key = { it.trackId },
            contentType = { "track" }
        ) { track ->
            TrackRow(
                track = track,
                onClick = { onTrackClick(track) }
            )
        }
    }
}

@Preview(name = "Empty state", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun FavoriteTracksScreenEmptyPreview() {
    AppTheme {
        FavoriteTracksScreen(
            state = FavoriteTracksState.Empty,
            onTrackClick = {}
        )
    }
}

@Preview(name = "Content state", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun FavoriteTracksScreenContentPreview() {
    AppTheme {
        FavoriteTracksScreen(
            state = FavoriteTracksState.Content(
                tracks = listOf(
                    Track(
                        trackName = "Song Title",
                        artistName = "Artist Name",
                        trackTimeMillis = 215000,
                        artworkUrl100 = "",
                        trackId = 1,
                        collectionName = "Album",
                        releaseDate = "1990",
                        primaryGenreName = "Rock",
                        country = "US",
                        isFavorite = true,
                        previewUrl = ""
                    ),
                    Track(
                        trackName = "Song Title 2",
                        artistName = "Artist Name",
                        trackTimeMillis = 201000,
                        artworkUrl100 = "",
                        trackId = 2,
                        collectionName = "Album",
                        releaseDate = "1998",
                        primaryGenreName = "Rock",
                        country = "US",
                        isFavorite = false,
                        previewUrl = ""
                    ),
                )
            ),
            onTrackClick = {}
        )
    }
}