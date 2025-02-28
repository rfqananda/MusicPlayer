package com.example.features.mainscreen.data.mapper

import com.example.core.networking.service.response.search_music.SearchMusicModelResponse
import com.example.core.networking.service.response.search_music.TrackResponse
import com.example.features.mainscreen.domain.model.SearchMusicModelDomain
import com.example.features.mainscreen.domain.model.TrackDomain

fun SearchMusicModelResponse.toDomain() = SearchMusicModelDomain(
    resultCount = resultCount ?: 0,
    results = results?.map { it.toDomain() }.orEmpty()
)

fun TrackResponse.toDomain() = TrackDomain(
    songImage = songImage.orEmpty(),
    songName = songName.orEmpty(),
    artist = artist.orEmpty(),
    album = album.orEmpty(),
    previewUrl = previewUrl.orEmpty()
)
