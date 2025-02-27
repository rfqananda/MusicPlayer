package com.example.core.networking.service

import com.example.core.networking.service.response.games_news.GNResponseItem
import com.example.core.wrapper.NetworkResultWrapper
import retrofit2.http.GET
import retrofit2.http.Query

interface GamesNewsService {
    @GET("/api/games")
    suspend fun getGamesNews(@Query("page") pageNumber: Int): NetworkResultWrapper<List<GNResponseItem>>
}
