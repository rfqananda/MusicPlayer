package com.example.features.mainscreen.ui.composeable

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.features.mainscreen.ui.composeable.SearchBarConstants.CLOSE
import com.example.features.mainscreen.ui.composeable.SearchBarConstants.QUERY_EMPTY
import com.example.features.mainscreen.ui.composeable.SearchBarConstants.SEARCH
import com.example.features.mainscreen.ui.composeable.SearchBarConstants.SEARCH_DESC
import com.example.features.mainscreen.utils.PlayerManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicSearchBar(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    active: Boolean,
    onActiveChange: (Boolean) -> Unit,
    containerColor: Color,
    playerManager: PlayerManager
) {
    SearchBar(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        query = searchQuery,
        onQueryChange = onQueryChange,
        onSearch = { onSearch(searchQuery) },
        active = active,
        onActiveChange = onActiveChange,
        placeholder = { Text(SEARCH) },
        leadingIcon = { Icon(Icons.Default.Search, SEARCH_DESC) },
        trailingIcon = {
            if (active) {
                IconButton(
                    onClick = {
                        onQueryChange(QUERY_EMPTY)
                        onActiveChange(false)
                        playerManager.reset()
                    }
                ) { Icon(Icons.Default.Close, CLOSE) }
            }
        },
        colors = SearchBarDefaults.colors(containerColor = containerColor)
    ) {}
}

private object SearchBarConstants {
    const val QUERY_EMPTY = ""
    const val CLOSE = "Close"
    const val SEARCH = "Search..."
    const val SEARCH_DESC = "search"
}
