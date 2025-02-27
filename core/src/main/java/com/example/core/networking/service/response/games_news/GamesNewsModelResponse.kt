package com.example.core.networking.service.response.games_news

data class GamesNewsModelResponse(
    val newsList: List<GNResponseItem>? = emptyList()
)

data class GNResponseItem(
    val author: String? = "",
    val desc: String? = "",
    val key: String? = "",
    val tag: String? = "",
    val thumb: String? = "",
    val time: String? = "",
    val title: String? = ""
)
