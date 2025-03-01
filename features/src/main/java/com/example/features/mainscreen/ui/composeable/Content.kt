package com.example.features.mainscreen.ui.composeable

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
import com.example.features.mainscreen.ui.composeable.ContentConstants.IMAGE_DESC
import com.example.features.mainscreen.ui.composeable.ContentConstants.ITEM_COUNT
import com.example.features.mainscreen.ui.model.SearchMusicModelUi
import com.example.features.mainscreen.ui.model.TrackUi
import com.example.features.mainscreen.ui.vm.SearchMusicViewModel
import com.example.features.mainscreen.utils.PlaybackState
import com.example.features.mainscreen.utils.PlayerManager
import com.example.uicomponent.R as component
import com.example.features.R as features
import com.example.uicomponent.empty_view.EmptyView
import com.example.uicomponent.error_view.ErrorView
import com.example.uicomponent.search_view.StartScreen
import com.example.uicomponent.shimmer.shimmerEffect

@Composable
fun MusicContent(
    modifier: Modifier = Modifier,
    viewModel: SearchMusicViewModel,
    searchQuery: String,
    playerManager: PlayerManager
) {
    val state by viewModel.searchMusic.collectAsStateWithLifecycle()
    val selectedPreviewUrl by viewModel.selectedPreviewUrl.collectAsStateWithLifecycle()
    val playTrigger by viewModel.playTrigger.collectAsState()

    LaunchedEffect(selectedPreviewUrl, playTrigger) {
        selectedPreviewUrl?.let(playerManager::preparePlayer)
    }
    if (searchQuery.isNotEmpty()) {
        HandleDataState(
            modifier = modifier,
            state = state,
            viewModel = viewModel,
            searchQuery = searchQuery,
            playerManager = playerManager
        )
    } else {
        viewModel.setListEmpty()
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
            items(ITEM_COUNT) {
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
    val color = Color(ContextCompat.getColor(context, component.color.blueSecondary))

    val playbackState by playerManager.playbackState.collectAsState()

    val backgroundColor by animateColorAsState(
        targetValue = if (item.isSelected) color else Color.Transparent
    )

    val isMarqueeEnabled = item.isSelected && playbackState == PlaybackState.PLAYING

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
                contentDescription = IMAGE_DESC,
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
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.Black,
                            modifier = Modifier
                                .fillMaxWidth()
                                .then(if (isMarqueeEnabled) Modifier.basicMarquee() else Modifier),
                            maxLines = 1,
                            overflow = if (isMarqueeEnabled) {
                                TextOverflow.Clip
                            } else {
                                TextOverflow.Ellipsis
                            },
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = item.artist,
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.Black,
                            modifier = Modifier
                                .fillMaxWidth()
                                .then(if (isMarqueeEnabled) Modifier.basicMarquee() else Modifier),
                            maxLines = 1,
                            overflow = if (isMarqueeEnabled) {
                                TextOverflow.Clip
                            } else {
                                TextOverflow.Ellipsis
                            },
                            fontSize = 16.sp
                        )

                        Text(
                            text = item.album,
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.Gray,
                            modifier = Modifier
                                .fillMaxWidth()
                                .then(if (isMarqueeEnabled) Modifier.basicMarquee() else Modifier),
                            maxLines = 1,
                            overflow = if (isMarqueeEnabled) {
                                TextOverflow.Clip
                            } else {
                                TextOverflow.Ellipsis
                            },
                            fontSize = 16.sp
                        )
                    }
                    if (isMarqueeEnabled) {
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
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(features.raw.sound))
    LottieAnimation(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        modifier = Modifier
            .height(60.dp)
            .width(60.dp)
    )
}

private object ContentConstants {
    const val IMAGE_DESC = "musicItemImage"
    const val ITEM_COUNT = 10
}
