package com.example.features.mainscreen.ui.model

data class SearchMusicModelUi(
    val resultCount: Int = 0,
    val results: List<TrackUi> = emptyList()
)

data class TrackUi(
    val isSelected: Boolean = false,
    val songImage: String = "",
    val songName: String = "",
    val artist: String = "",
    val album: String = "",
    val previewUrl: String = ""
)
