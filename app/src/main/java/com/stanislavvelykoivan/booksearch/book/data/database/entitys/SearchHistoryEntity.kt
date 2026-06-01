package com.stanislavvelykoivan.booksearch.book.data.database.entitys

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_history")
data class SearchHistoryEntity(
    @PrimaryKey
    val searchQuery: String,
    val lastSearchedAt: Long
)