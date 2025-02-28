package com.example.features.mainscreen.domain.repository

import com.example.core.utils.DomainResult
import com.example.features.mainscreen.domain.model.SearchMusicModelDomain

interface SearchMusicRepository {
    suspend fun getSearchMusic(term: String): DomainResult<SearchMusicModelDomain>
}
