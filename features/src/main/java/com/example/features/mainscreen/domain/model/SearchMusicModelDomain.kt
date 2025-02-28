package com.example.features.mainscreen.domain.model

data class SearchMusicModelDomain(
    val resultCount: Int? = 0,
    val results: List<TrackDomain>? = emptyList()
)

data class TrackDomain(
    val songImage: String? = "",
    val songName: String? = "",
    val artist: String? = "",
    val album: String? = "",
    val previewUrl: String? = ""
)
