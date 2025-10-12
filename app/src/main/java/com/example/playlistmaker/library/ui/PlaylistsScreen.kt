package com.example.playlistmaker.library.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.library.domain.model.Playlist
import com.example.playlistmaker.library.ui.models.PlaylistsState
import com.example.playlistmaker.theme.AppTheme

private val YSDisplayMedium = FontFamily(Font(R.font.ys_display_medium, weight = FontWeight.Medium))

@Composable
fun PlaylistsScreen(
    state: PlaylistsState,
    onNewPlaylistClick: () -> Unit,
    onPlaylistClick: (Playlist) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Button(
            onClick = onNewPlaylistClick,
            modifier = Modifier
                .wrapContentHeight()
                .align(Alignment.CenterHorizontally)
                .padding(top = dimensionResource(R.dimen.margin_elements)),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.onSecondary
            ),
            shape = RoundedCornerShape(54.dp),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text = stringResource(R.string.new_playlist),
                color = MaterialTheme.colorScheme.secondary,
                fontFamily = YSDisplayMedium,
                fontSize = 14.sp,
                lineHeight = 14.sp,
                textAlign = TextAlign.Center
            )
        }

        when (state) {
            is PlaylistsState.Empty -> {
                EmptyPlaylistsContent()
            }
            is PlaylistsState.Content -> {
                PlaylistsGrid(
                    playlists = state.playlists,
                    onPlaylistClick = onPlaylistClick
                )
            }
        }
    }
}

@Composable
private fun PlaylistsGrid(
    playlists: List<Playlist>,
    onPlaylistClick: (Playlist) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        contentPadding = PaddingValues(vertical = 0.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(playlists) { playlist ->
            PlaylistItem(
                playlist = playlist,
                onClick = { onPlaylistClick(playlist) }
            )
        }
    }
}

@Composable
private fun EmptyPlaylistsContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.no_result),
            contentDescription = null,
            modifier = Modifier.wrapContentSize()
        )

        Text(
            text = stringResource(R.string.playlists_is_empty),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSecondary,
            textAlign = TextAlign.Center,
            fontFamily = YSDisplayMedium,
            fontSize = dimensionResource(R.dimen.placeholder_text_size).value.sp,
            modifier = Modifier.padding(top = dimensionResource(R.dimen.margin_button))
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun PlaylistsScreenEmptyPreview() {
    AppTheme {
        PlaylistsScreen(
            state = PlaylistsState.Empty,
            onNewPlaylistClick = {},
            onPlaylistClick = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun PlaylistsScreenContentPreview() {
    AppTheme {
        PlaylistsScreen(
            state = PlaylistsState.Content(
                playlists = listOf(
                    Playlist(
                        playlistId = 1,
                        playlistName = "Best songs",
                        playlistDescription = "My favorite tracks",
                        imagePath = "drawable://${R.drawable.track_placeholder}",
                        trackIdList = listOf(1, 2, 3),
                        tracksCount = 25
                    ),
                    Playlist(
                        playlistId = 2,
                        playlistName = "Workout Mix",
                        playlistDescription = "Energy music for sports",
                        imagePath = "drawable://${R.drawable.track_placeholder}",
                        trackIdList = listOf(4, 5, 6, 7),
                        tracksCount = 18
                    ),
                    Playlist(
                        playlistId = 3,
                        playlistName = "Relax",
                        playlistDescription = "Calm music for rest",
                        imagePath = "drawable://${R.drawable.track_placeholder}",
                        trackIdList = listOf(8, 9),
                        tracksCount = 12
                    ),
                    Playlist(
                        playlistId = 4,
                        playlistName = "Party Time",
                        playlistDescription = "Dance music",
                        imagePath = "drawable://${R.drawable.track_placeholder}",
                        trackIdList = listOf(10, 11, 12),
                        tracksCount = 30
                    )
                )
            ),
            onNewPlaylistClick = {},
            onPlaylistClick = {}
        )
    }
}