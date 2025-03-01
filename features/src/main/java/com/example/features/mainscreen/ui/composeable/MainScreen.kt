package com.example.features.mainscreen.ui.composeable

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.core.state.UiSafeState
import com.example.features.R as featRes
import com.example.uicomponent.R as uiRes
import com.example.features.mainscreen.ui.model.SearchMusicModelUi
import com.example.features.mainscreen.ui.model.TrackUi
import com.example.features.mainscreen.ui.vm.SearchMusicViewModel
import com.example.features.mainscreen.utils.PlaybackState
import com.example.features.mainscreen.utils.PlayerManager
import com.example.uicomponent.R.color
import com.example.uicomponent.empty_view.EmptyView
import com.example.uicomponent.error_view.ErrorView
import com.example.uicomponent.search_view.StartScreen
import com.example.uicomponent.shimmer.shimmerEffect
import org.koin.androidx.compose.koinViewModel
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.LaunchedEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenMusicPlayer(viewModel: SearchMusicViewModel = koinViewModel()) {

    var searchQuery by remember { mutableStateOf("") }
    var active by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val color = Color(ContextCompat.getColor(context, color.bottomBarColor))

    val playerManager = remember { PlayerManager(context, coroutineScope) }

    val selectedPreviewUrl by viewModel.selectedPreviewUrl.collectAsState()

    LaunchedEffect(selectedPreviewUrl) {
        selectedPreviewUrl?.let { url ->
            playerManager.preparePlayer(url)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Box(modifier = Modifier.padding(horizontal = 10.dp)) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    onSearch = {
                        viewModel.searchMusic(it)
                        active = false
                    },
                    active = active,
                    onActiveChange = { active = it },
                    placeholder = { Text("Search...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (active) {
                            IconButton(onClick = {
                                searchQuery = ""
                                active = false
                            }) {
                                Icon(Icons.Default.Close, contentDescription = "Tutup")
                            }
                        }
                    },
                    colors = SearchBarDefaults.colors(
                        containerColor = color
                    )
                ) {}
            }
        },
        bottomBar = {
            BottomController(playerManager = playerManager, viewModel = viewModel)
        }
    )
    { innerPadding ->
        MainContent(
            modifier = Modifier.padding(innerPadding),
            viewModel = viewModel,
            searchQuery = searchQuery,
            playerManager = playerManager
        )
    }
}

@Composable
private fun MainContent(
    modifier: Modifier = Modifier,
    viewModel: SearchMusicViewModel,
    searchQuery: String,
    playerManager: PlayerManager
) {
    val state by viewModel.searchMusic.collectAsStateWithLifecycle()
    if (searchQuery.isNotEmpty()) {
        HandleDataState(
            modifier = modifier,
            state = state,
            viewModel = viewModel,
            searchQuery = searchQuery,
            playerManager = playerManager
        )
    } else {
        StartScreen(modifier = modifier)
    }
}

@Composable
private fun HandleDataState(
    modifier: Modifier,
    state: UiSafeState<SearchMusicModelUi>,
    viewModel: SearchMusicViewModel,
    searchQuery: String,
    playerManager: PlayerManager
) {
    ShimmerListItem(
        modifier = modifier,
        isLoading = state is UiSafeState.Loading,
        contentAfterLoading = {
            when (state) {
                is UiSafeState.Success -> {
                    val data = state.data.results
                    if (data.isNotEmpty()) {
                        ListMusic(
                            modifier = modifier,
                            data = data,
                            onItemClick = { position ->
                                viewModel.onItemSelected(position)
                            },
                            playerManager = playerManager
                        )
                    } else {
                        EmptyView(modifier = modifier)
                    }
                }

                is UiSafeState.Error -> {
                    ErrorView(
                        onRetry = {
                            viewModel.searchMusic(searchQuery)
                        }
                    )
                }

                is UiSafeState.Empty -> {
                    EmptyView(modifier = modifier)
                }

                else -> {}
            }
        }
    )
}

@Composable
private fun ShimmerListItem(
    modifier: Modifier = Modifier,
    isLoading: Boolean,
    contentAfterLoading: @Composable () -> Unit
) {
    if (isLoading) {
        LazyColumn(modifier = modifier.fillMaxWidth()) {
            items(10) {
                ShimmerItem()
            }
        }
    } else {
        contentAfterLoading()
    }
}

@Composable
private fun ShimmerItem() {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
                .padding(vertical = 20.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(115.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .shimmerEffect()
            )

            Spacer(modifier = Modifier.width(10.dp))

            Box(
                modifier = Modifier
                    .height(115.dp)
                    .fillMaxWidth()
                    .padding(end = 10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .align(Alignment.TopStart)
                        .shimmerEffect()
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .align(Alignment.CenterStart)
                        .shimmerEffect()
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .align(Alignment.BottomStart)
                        .shimmerEffect()
                )
            }
        }
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = Color.Gray
        )
    }
}

@Composable
private fun ListMusic(
    modifier: Modifier,
    data: List<TrackUi>,
    onItemClick: (Int) -> Unit,
    playerManager: PlayerManager
) {
    LazyColumn(modifier = modifier) {
        itemsIndexed(data) { index, track ->
            MusicItem(
                item = track,
                onClick = { onItemClick(index) },
                playerManager = playerManager
            )
        }
    }
}

@Composable
private fun MusicItem(
    item: TrackUi = TrackUi(),
    onClick: () -> Unit,
    playerManager: PlayerManager
) {
    val context = LocalContext.current
    val color = Color(ContextCompat.getColor(context, color.blueSecondary))

    val playbackState by playerManager.playbackState.collectAsState()

    val backgroundColor by animateColorAsState(
        targetValue = if (item.isSelected) color else Color.Transparent
    )
    Column(modifier = Modifier
        .background(color = backgroundColor)
        .clickable { onClick() }
        .padding(horizontal = 20.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
                .padding(vertical = 20.dp)
        ) {
            AsyncImage(
                model = item.songImage,
                contentDescription = "music item image",
                modifier = Modifier
                    .height(115.dp)
                    .width(115.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .height(115.dp)
                    .fillMaxWidth()
                    .padding(end = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = item.songName,
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.Black,
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 16.sp
                        )

                        Text(
                            text = item.artist,
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.Black,
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 16.sp
                        )

                        Text(
                            text = item.album,
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.Black,
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 16.sp
                        )
                    }
                    if (item.isSelected && playbackState == PlaybackState.PLAYING) {
                        InfiniteLoopLottieAnimation()
                    }
                }
            }
        }
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = Color.Gray
        )
    }
}

@Composable
private fun InfiniteLoopLottieAnimation() {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(featRes.raw.sound))
    LottieAnimation(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        modifier = Modifier
            .height(60.dp)
            .width(60.dp)
    )
}

@Composable
private fun BottomController(playerManager: PlayerManager, viewModel: SearchMusicViewModel) {
    val context = LocalContext.current
    val color = Color(ContextCompat.getColor(context, color.bottomBarColor))
    val colorTextSlider = Color(ContextCompat.getColor(context, uiRes.color.bluePrimary))
    val cornerRadius = 16.dp
    val elevation = 1.dp

    val playbackState by playerManager.playbackState.collectAsState()
    val currentPosition by playerManager.currentPosition.collectAsState()
    val duration by playerManager.duration.collectAsState()
    var sliderPosition by remember { mutableStateOf(0f) }
    var isSliding by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier

            .clip(RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius))
            .background(color)
            .fillMaxWidth()
            .shadow(
                elevation = elevation,
                shape = RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius)
            )
            .navigationBarsPadding()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Column(modifier = Modifier.fillMaxWidth()) {
            Slider(
                value = if (isSliding) sliderPosition
                else currentPosition.coerceIn(0, duration).toFloat(),
                onValueChange = { newValue ->
                    sliderPosition = newValue
                    isSliding = true
                },
                onValueChangeFinished = {
                    playerManager.seekTo(sliderPosition.toLong())
                    isSliding = false
                },
                valueRange = 0f..duration.toFloat(),
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color.White,
                    inactiveTrackColor = Color.Gray.copy(alpha = 0.5f)
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatTime(currentPosition),
                    color = colorTextSlider,
                    fontSize = 12.sp
                )
                Text(
                    text = formatTime(duration),
                    color = colorTextSlider,
                    fontSize = 12.sp
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(modifier = Modifier.size(30.dp), onClick = {
                viewModel.previousTrack()
            }) {
                Icon(
                    painter = painterResource(id = featRes.drawable.back),
                    contentDescription = "Previous",
                    tint = Color.Unspecified
                )
            }

            IconButton(modifier = Modifier.size(60.dp), onClick = {
                playerManager.playPause()
            }) {
                Icon(
                    painter = if (playbackState == PlaybackState.PLAYING) painterResource(id = featRes.drawable.pause)
                    else painterResource(id = featRes.drawable.play),
                    contentDescription = "Play/Pause",
                    tint = Color.Unspecified
                )
            }

            IconButton(modifier = Modifier.size(30.dp), onClick = {
                viewModel.nextTrack()
            }) {
                Icon(
                    painter = painterResource(id = featRes.drawable.skip),
                    contentDescription = "Next",
                    tint = Color.Unspecified
                )
            }
        }
    }
}

@SuppressLint("DefaultLocale")
private fun formatTime(milliseconds: Long): String {
    val totalSeconds = milliseconds / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}

@Preview(showBackground = true)
@Composable
private fun PreviewMainScreen() {
}
