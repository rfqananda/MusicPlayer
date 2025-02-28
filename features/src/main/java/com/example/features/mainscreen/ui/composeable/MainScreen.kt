package com.example.features.mainscreen.ui.composeable

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.core.state.UiSafeState
import com.example.features.R
import com.example.features.mainscreen.ui.model.SearchMusicModelUi
import com.example.features.mainscreen.ui.model.TrackUi
import com.example.features.mainscreen.ui.vm.SearchMusicViewModel
import com.example.uicomponent.shimmer.shimmerEffect
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScreenMusicPlayer() {
    Scaffold(modifier = Modifier.fillMaxSize())
    { innerPadding ->
        MainContent(modifier = Modifier.padding(innerPadding))
    }
}

@Composable
fun MainContent(
    viewModel: SearchMusicViewModel = koinViewModel(),
    modifier: Modifier = Modifier
){
    val state by viewModel.searchMusic.collectAsStateWithLifecycle()
    HandleDataState(state)
}

@Composable
private fun HandleDataState(
    state: UiSafeState<SearchMusicModelUi>,
) {

    ShimmerListItem(
        isLoading = state is UiSafeState.Loading,
        contentAfterLoading = {
            when (state) {
                is UiSafeState.Success -> {
                    val data = state.data.results
                    ListMusic(data)
                }
                is UiSafeState.Error -> {
                }

                is UiSafeState.Empty -> {
                }
                else -> {

                }
            }
        }
    )
}

@Composable
fun ShimmerListItem(
    isLoading: Boolean,
    contentAfterLoading: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isLoading) {
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(10) {
                ShimmerItem()
            }
        }
    } else {
        contentAfterLoading()
    }
}

@Composable
fun ShimmerItem() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(10.dp)
            .clip(RoundedCornerShape(16.dp))
            .shimmerEffect()
    )
}

@Composable
fun ListMusic(data: List<TrackUi>) {
    LazyColumn {
        items(data){
            MusicItem(
                isSelected = false,
                item = it,
                modifier = Modifier.clickable {

                }
            )
        }
    }
}

@Composable
fun MusicItem(
    isSelected: Boolean,
    item: TrackUi = TrackUi(),
    modifier: Modifier
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(10.dp)
    ) {
        AsyncImage(
            model = item.songImage,
            contentDescription = "",
            modifier = Modifier
                .height(115.dp)
                .width(115.dp)
                .clip(RoundedCornerShape(10.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(10.dp))
        Box(modifier = Modifier.height(115.dp).fillMaxWidth().padding(end = 10.dp)) {
            Text(
                text = item.songName,
                modifier = Modifier.align(Alignment.TopStart),
                fontSize = 16.sp,
                color = Color.Black,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = item.artist,
                modifier = Modifier.align(Alignment.CenterStart),
                fontSize = 16.sp,
                color = Color.Black,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = item.album,
                modifier = Modifier.align(Alignment.BottomStart),
                fontSize = 16.sp,
                color = Color.Black,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (isSelected) {
                Image(
                    painter = painterResource(id = R.drawable.music),
                    contentDescription = "Gambar logo",
                    modifier = Modifier
                        .size(60.dp)
                        .align(Alignment.CenterEnd)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMainScreen() {
//    MusicItem(true)
}
