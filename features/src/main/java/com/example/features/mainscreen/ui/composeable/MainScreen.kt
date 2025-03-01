package com.example.features.mainscreen.ui.composeable

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.example.features.mainscreen.ui.composeable.MainScreenConstants.EMPTY_VALUE
import com.example.features.mainscreen.ui.vm.SearchMusicViewModel
import com.example.features.mainscreen.utils.PlayerManager
import org.koin.androidx.compose.koinViewModel
import com.example.uicomponent.R as uiRes

@Composable
fun MainScreenMusicPlayer(
    playerManager: PlayerManager,
    viewModel: SearchMusicViewModel = koinViewModel()
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf(EMPTY_VALUE) }
    var isSearchActive by remember { mutableStateOf(false) }

    val containerColor = remember(context) {
        Color(ContextCompat.getColor(context, uiRes.color.bottomBarColor))
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            MusicSearchBar(
                searchQuery = searchQuery,
                onQueryChange = { searchQuery = it },
                onSearch = { viewModel.searchMusic(it); isSearchActive = false },
                active = isSearchActive,
                onActiveChange = { isSearchActive = it },
                containerColor = containerColor,
                playerManager = playerManager
            )
        },
        bottomBar = {
            PlayerController(
                playerManager = playerManager,
                viewModel = viewModel
            )
        }
    ) { padding ->
        MusicContent(
            modifier = Modifier.padding(padding),
            viewModel = viewModel,
            searchQuery = searchQuery,
            playerManager = playerManager
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewMainScreen() {
}

private object MainScreenConstants {
    const val EMPTY_VALUE = ""
}
