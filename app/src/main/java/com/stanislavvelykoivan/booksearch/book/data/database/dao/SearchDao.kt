package com.stanislavvelykoivan.booksearch.book.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.stanislavvelykoivan.booksearch.book.data.database.entitys.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSearchQuery(searchHistory: SearchHistoryEntity)

    @Query("SELECT * FROM search_history ORDER BY lastSearchedAt DESC LIMIT 20")
    fun getRecentSearches(): Flow<List<SearchHistoryEntity>>

    @Query("DELETE FROM search_history WHERE searchQuery = :query")
    suspend fun deleteSearchQuery(query: String)
}