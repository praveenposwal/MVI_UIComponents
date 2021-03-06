package com.ezike.tobenna.starwarssearch.data.contract.cache

import com.ezike.tobenna.starwarssearch.data.model.CharacterEntity

interface SearchHistoryCache {
    suspend fun saveSearch(character: CharacterEntity)
    suspend fun getSearchHistory(): List<CharacterEntity>
    suspend fun clearSearchHistory()
}
