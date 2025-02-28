package com.example.features.mainscreen.ui.mapper

import com.example.features.mainscreen.domain.model.SearchMusicModelDomain
import com.example.features.mainscreen.domain.model.TrackDomain
import com.example.features.mainscreen.ui.model.SearchMusicModelUi
import com.example.features.mainscreen.ui.model.TrackUi


fun SearchMusicModelDomain.toUi() = SearchMusicModelUi(
    resultCount = resultCount ?: 0,
    results = results?.map { it.toUi() }.orEmpty()
)

fun TrackDomain.toUi() = TrackUi(
    songImage = songImage.orEmpty(),
    songName = songName.orEmpty(),
    artist = artist.orEmpty(),
    album = album.orEmpty(),
    previewUrl = previewUrl.orEmpty()
)
