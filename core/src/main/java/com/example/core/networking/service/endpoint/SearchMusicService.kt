package com.example.core.networking.service.endpoint

import com.example.core.networking.service.response.search_music.SearchMusicModelResponse
import com.example.core.wrapper.NetworkResultWrapper
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchMusicService {

    @GET("search")
    suspend fun searchSongs(
        @Query("term") term: String,
        @Query("entity") entity: String = "song"
    ): NetworkResultWrapper<SearchMusicModelResponse>

}
