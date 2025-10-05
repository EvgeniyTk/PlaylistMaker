package com.example.playlistmaker.search.ui

import androidx.annotation.DimenRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.models.TracksState
import kotlinx.coroutines.flow.StateFlow
import androidx.compose.runtime.remember
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import com.example.playlistmaker.theme.AppTheme
import com.example.playlistmaker.search.view_model.SearchViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.LayoutDirection
import com.example.playlistmaker.theme.LocalAppExtraColors

private val YSDisplayRegular =
    FontFamily(Font(R.font.ys_display_regular, weight = FontWeight.Normal))
private val YSDisplayMedium = FontFamily(Font(R.font.ys_display_medium, weight = FontWeight.Medium))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    stateFlow: StateFlow<TracksState>,
    queryFlow: StateFlow<String>,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    onRetry: () -> Unit,
    onClearHistory: () -> Unit,
    onTrackClick: (Track) -> Unit,
    onInputFocusChange: (Boolean) -> Unit
) {
    val state by stateFlow.collectAsState()
    val query by queryFlow.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {

            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary,
                    titleContentColor = LocalAppExtraColors.current.textColorSecondary
                ),
                title = {
                    Text(
                        text = stringResource(id = R.string.search),
                        fontSize = dimenSp(R.dimen.primary_text_size),
                        fontFamily = YSDisplayMedium,
                        fontWeight = FontWeight.Medium
                    )
                },
                modifier = Modifier.sizeIn(minHeight = 56.dp),
                windowInsets = WindowInsets.statusBars
            )
        },
        containerColor = MaterialTheme.colorScheme.secondary,
        contentWindowInsets = WindowInsets(0,0,0,0)

        ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = innerPadding.calculateTopPadding(),
                    start = innerPadding.calculateStartPadding(LayoutDirection.Ltr),
                    end = innerPadding.calculateEndPadding(LayoutDirection.Ltr)
                )
        ) {
            SearchBasicField(
                query = query,
                onQueryChange = onQueryChange,
                onClearQuery = onClearQuery,
                onInputFocusChange = onInputFocusChange,
                keyboardController = keyboardController,
                focusManager = focusManager
            )


            LaunchedEffect(Unit) { onInputFocusChange(false) }

            when (val s = state) {
                is TracksState.Loading -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Spacer(Modifier.height(140.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(44.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                is TracksState.Error -> {
                    ErrorPlaceholder(
                        code = s.code,
                        onRetry = onRetry
                    )
                }

                is TracksState.Content -> {
                    TrackList(
                        tracks = s.list,
                        onTrackClick = onTrackClick
                    )
                }

                is TracksState.History -> {
                    HistoryBlock(
                        tracks = s.list,
                        onClearHistory = onClearHistory,
                        onTrackClick = onTrackClick
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryBlock(
    tracks: List<Track>,
    onClearHistory: () -> Unit,
    onTrackClick: (Track) -> Unit
) {
    if (tracks.isNotEmpty()) {
        Text(
            text = stringResource(id = R.string.search_history_header),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimenDp(R.dimen.padding_from_edges))
                .padding(top = 24.dp)
                .padding(bottom = dimenDp(R.dimen.margin_editText_vertical)),
            fontSize = dimenSp(R.dimen.placeholder_text_size),
            fontFamily = YSDisplayMedium,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )

        TrackList(tracks = tracks, onTrackClick = onTrackClick)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = dimenDp(R.dimen.margin_elements)),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = onClearHistory,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(54.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onSecondary,
                    contentColor = MaterialTheme.colorScheme.secondary
                ),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.clear_history_button),
                    fontFamily = YSDisplayMedium
                )
            }
        }
    } else {

        TrackList(tracks = tracks, onTrackClick = onTrackClick)
    }
}

@Composable
private fun ErrorPlaceholder(
    code: SearchViewModel.CodeError,
    onRetry: () -> Unit
) {
    val (imageRes, textRes, showButton) = when (code) {
        SearchViewModel.CodeError.NORESULT ->
            Triple(R.drawable.no_result, R.string.error_result_search, false)

        SearchViewModel.CodeError.BADCONNECTION ->
            Triple(R.drawable.bad_connection, R.string.error_bad_connection, true)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier.padding(vertical = dimenDp(R.dimen.padding_from_edges))
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(id = textRes),
            fontSize = dimenSp(R.dimen.placeholder_text_size),
            fontFamily = YSDisplayMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = dimenDp(R.dimen.margin_elements)),
            color = MaterialTheme.colorScheme.onSurface
        )
        if (showButton) {
            Spacer(Modifier.height(dimenDp(R.dimen.margin_elements)))
            Button(
                onClick = onRetry,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(54.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onSecondary,
                    contentColor = MaterialTheme.colorScheme.secondary
                ),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.update),
                    fontFamily = YSDisplayMedium
                )
            }
        }
    }
}

@Composable
fun SearchBasicField(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    onInputFocusChange: (Boolean) -> Unit,
    keyboardController: SoftwareKeyboardController?,
    focusManager: FocusManager
) {
    val textStyle = TextStyle(
        fontSize = dimenSp(R.dimen.secondary_text_size),
        fontFamily = YSDisplayRegular,
        color = MaterialTheme.colorScheme.onSurface
    )

    Surface(
        modifier = Modifier.padding(
            horizontal = dimenDp(R.dimen.padding_editText_horizontal),
            vertical = dimenDp(R.dimen.margin_editText_vertical),
        )
            .height(36.dp),
        shape = RoundedCornerShape(8.dp),
        color = LocalAppExtraColors.current.editTextBackgroundColor
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = dimenDp(R.dimen.padding_editText_vertical))
                .onFocusChanged { onInputFocusChange(it.isFocused) },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.edittext_search),
                contentDescription = null,
                tint = LocalAppExtraColors.current.textColorSearchHint,
                modifier = Modifier.padding(start = dimenDp(R.dimen.padding_editText_horizontal))
            )

            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                maxLines = 1,
                textStyle = textStyle.copy(colorResource(R.color.black_app)),
                cursorBrush = SolidColor(LocalAppExtraColors.current.cursorColor),
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = dimenDp(R.dimen.padding_icon))
            ) { innerTextField ->
                if (query.isEmpty()) {
                    Text(
                        text = stringResource(id = R.string.search),
                        style = textStyle.copy(color = LocalAppExtraColors.current.textColorSearchHint)
                    )
                }
                innerTextField()
            }

            if (query.isNotEmpty()) {
                IconButton(
                    onClick = {
                        onClearQuery()
                        onQueryChange("")
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.clear_search),
                        contentDescription = stringResource(id = R.string.clear_history_button),
                        tint = LocalAppExtraColors.current.textColorSearchHint
                    )
                }
            } else {
                Spacer(Modifier.padding(end = dimenDp(R.dimen.padding_editText_horizontal)))
            }
        }
    }
}

@Composable
private fun TrackList(
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxWidth()
    ) {
        items(tracks, key = { it.trackId }) { track ->
            TrackRow(track = track, onClick = { onTrackClick(track) })
        }
    }
}

@Composable
private fun dimenDp(@DimenRes id: Int) = dimensionResource(id)

@Composable
private fun dimenSp(@DimenRes id: Int) = dimenDp(id).value.sp


@Preview(showBackground = true)
@Composable
fun SearchScreenContentPreview() {
    val dummyTracks = listOf(
        Track(
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
        ),
        Track(
            trackName = "Song Title",
            artistName = "Artist Name",
            trackTimeMillis = 215000,
            artworkUrl100 = "drawable://${R.drawable.track_placeholder}",
            trackId = 2,
            collectionName = "1",
            releaseDate = "1990",
            primaryGenreName = "TODO()",
            country = "TODO()",
            isFavorite = true,
            previewUrl = "TODO()"
        ),
        Track(
            trackName = "Song Title",
            artistName = "Artist Name",
            trackTimeMillis = 215000,
            artworkUrl100 = "drawable://${R.drawable.track_placeholder}",
            trackId = 3,
            collectionName = "1",
            releaseDate = "1990",
            primaryGenreName = "TODO()",
            country = "TODO()",
            isFavorite = true,
            previewUrl = "TODO()"
        ),
        Track(
            trackName = "Song Title",
            artistName = "Artist Name",
            trackTimeMillis = 215000,
            artworkUrl100 = "drawable://${R.drawable.track_placeholder}",
            trackId = 4,
            collectionName = "1",
            releaseDate = "1990",
            primaryGenreName = "TODO()",
            country = "TODO()",
            isFavorite = true,
            previewUrl = "TODO()"
        ),
        Track(
            trackName = "Song Title",
            artistName = "Artist Name",
            trackTimeMillis = 215000,
            artworkUrl100 = "drawable://${R.drawable.track_placeholder}",
            trackId = 5,
            collectionName = "1",
            releaseDate = "1990",
            primaryGenreName = "TODO()",
            country = "TODO()",
            isFavorite = true,
            previewUrl = "TODO()"
        ),
        Track(
            trackName = "Song Title",
            artistName = "Artist Name",
            trackTimeMillis = 215000,
            artworkUrl100 = "drawable://${R.drawable.track_placeholder}",
            trackId = 6,
            collectionName = "1",
            releaseDate = "1990",
            primaryGenreName = "TODO()",
            country = "TODO()",
            isFavorite = true,
            previewUrl = "TODO()"
        ),
        Track(
            trackName = "Song Title",
            artistName = "Artist Name",
            trackTimeMillis = 215000,
            artworkUrl100 = "drawable://${R.drawable.track_placeholder}",
            trackId = 7,
            collectionName = "1",
            releaseDate = "1990",
            primaryGenreName = "TODO()",
            country = "TODO()",
            isFavorite = true,
            previewUrl = "TODO()"
        ),
        Track(
            trackName = "Song Title",
            artistName = "Artist Name",
            trackTimeMillis = 215000,
            artworkUrl100 = "drawable://${R.drawable.track_placeholder}",
            trackId = 8,
            collectionName = "1",
            releaseDate = "1990",
            primaryGenreName = "TODO()",
            country = "TODO()",
            isFavorite = true,
            previewUrl = "TODO()"
        ),
        Track(
            trackName = "Song Title",
            artistName = "Artist Name",
            trackTimeMillis = 215000,
            artworkUrl100 = "drawable://${R.drawable.track_placeholder}",
            trackId = 9,
            collectionName = "1",
            releaseDate = "1990",
            primaryGenreName = "TODO()",
            country = "TODO()",
            isFavorite = true,
            previewUrl = "TODO()"
        ),
        Track(
            trackName = "Song Title",
            artistName = "Artist Name",
            trackTimeMillis = 215000,
            artworkUrl100 = "drawable://${R.drawable.track_placeholder}",
            trackId = 10,
            collectionName = "1",
            releaseDate = "1990",
            primaryGenreName = "TODO()",
            country = "TODO()",
            isFavorite = true,
            previewUrl = "TODO()"
        ),
        Track(
            trackName = "Song Title",
            artistName = "Artist Name",
            trackTimeMillis = 215000,
            artworkUrl100 = "drawable://${R.drawable.track_placeholder}",
            trackId = 11,
            collectionName = "1",
            releaseDate = "1990",
            primaryGenreName = "TODO()",
            country = "TODO()",
            isFavorite = true,
            previewUrl = "TODO()"
        ),
        Track(
            trackName = "Song Title",
            artistName = "Artist Name",
            trackTimeMillis = 215000,
            artworkUrl100 = "drawable://${R.drawable.track_placeholder}",
            trackId = 12,
            collectionName = "1",
            releaseDate = "1990",
            primaryGenreName = "TODO()",
            country = "TODO()",
            isFavorite = true,
            previewUrl = "TODO()"
        ),
        Track(
            trackName = "Song Title",
            artistName = "Artist Name",
            trackTimeMillis = 215000,
            artworkUrl100 = "drawable://${R.drawable.track_placeholder}",
            trackId = 13,
            collectionName = "1",
            releaseDate = "1990",
            primaryGenreName = "TODO()",
            country = "TODO()",
            isFavorite = true,
            previewUrl = "TODO()"
        )
    )

    val dummyStateFlow = remember { MutableStateFlow(TracksState.Content(dummyTracks)) }
    val dummyQueryFlow = remember { MutableStateFlow("") }

    AppTheme {
        SearchScreen(
            stateFlow = dummyStateFlow,
            queryFlow = dummyQueryFlow,
            onQueryChange = {},
            onClearQuery = {},
            onRetry = {},
            onClearHistory = {},
            onTrackClick = {},
            onInputFocusChange = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenHistoryPreview() {
    val dummyHistory = listOf(
        Track(
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
        ),
        Track(
            trackName = "Song Title",
            artistName = "Artist Name",
            trackTimeMillis = 215000,
            artworkUrl100 = "drawable://${R.drawable.track_placeholder}",
            trackId = 2,
            collectionName = "1",
            releaseDate = "1990",
            primaryGenreName = "TODO()",
            country = "TODO()",
            isFavorite = true,
            previewUrl = "TODO()"
        )
    )

    val dummyStateFlow = remember { MutableStateFlow(TracksState.History(dummyHistory)) }
    val dummyQueryFlow = remember { MutableStateFlow("") }

    AppTheme {
        SearchScreen(
            stateFlow = dummyStateFlow,
            queryFlow = dummyQueryFlow,
            onQueryChange = {},
            onClearQuery = {},
            onRetry = {},
            onClearHistory = {},
            onTrackClick = {},
            onInputFocusChange = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenErrorPreview() {
    val dummyStateFlow = remember {
        MutableStateFlow(
            TracksState.Error(SearchViewModel.CodeError.BADCONNECTION)
        )
    }
    val dummyQueryFlow = remember { MutableStateFlow("") }

    AppTheme {
        SearchScreen(
            stateFlow = dummyStateFlow,
            queryFlow = dummyQueryFlow,
            onQueryChange = {},
            onClearQuery = {},
            onRetry = {},
            onClearHistory = {},
            onTrackClick = {},
            onInputFocusChange = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchScreenLoadingPreview() {
    val dummyStateFlow = remember { MutableStateFlow(TracksState.Loading) }
    val dummyQueryFlow = remember { MutableStateFlow("") }

    AppTheme {
        SearchScreen(
            stateFlow = dummyStateFlow,
            queryFlow = dummyQueryFlow,
            onQueryChange = {},
            onClearQuery = {},
            onRetry = {},
            onClearHistory = {},
            onTrackClick = {},
            onInputFocusChange = {}
        )
    }
}