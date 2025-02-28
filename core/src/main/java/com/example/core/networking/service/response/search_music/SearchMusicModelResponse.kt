package com.example.core.networking.service.response.search_music

import com.google.gson.annotations.SerializedName

data class SearchMusicModelResponse(
    val resultCount: Int? = 0,
    val results: List<TrackResponse>? = emptyList()
)

data class TrackResponse(
    @SerializedName("artworkUrl100") val songImage: String? = "",
    @SerializedName("trackName") val songName: String? = "",
    @SerializedName("artistName") val artist: String? = "",
    @SerializedName("collectionName") val album: String? = "",
    @SerializedName("previewUrl") val previewUrl: String? = ""
)
