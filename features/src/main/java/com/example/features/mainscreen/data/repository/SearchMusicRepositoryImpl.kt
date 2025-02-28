package com.example.features.mainscreen.data.repository

import com.example.core.networking.dispatcher.CoroutinesDispatcherProvider
import com.example.core.networking.service.endpoint.SearchMusicService
import com.example.core.utils.DomainResult
import com.example.core.utils.processResponse
import com.example.features.mainscreen.data.mapper.toDomain
import com.example.features.mainscreen.domain.model.SearchMusicModelDomain
import com.example.features.mainscreen.domain.repository.SearchMusicRepository
import kotlinx.coroutines.withContext

class SearchMusicRepositoryImpl(
    private val searchMusicService: SearchMusicService,
    private val dispatchers: CoroutinesDispatcherProvider
): SearchMusicRepository {

    override suspend fun getSearchMusic(term: String): DomainResult<SearchMusicModelDomain> =
        withContext(dispatchers.io){
            return@withContext processResponse(searchMusicService.searchSongs(term)) {
                DomainResult.Success(it.toDomain())
            }
        }

}
