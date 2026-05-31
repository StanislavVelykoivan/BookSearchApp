package com.stanislavvelykoivan.booksearch.book.data.database.entitys

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "books")
data class BookEntity (
    @PrimaryKey(autoGenerate = false) val bookId: Long,
    val title: String,
    val languages: List<String>,
    val subjects: List<String>,
    val bookshelves: List<String>,
    val downloadCount: Int,
    val coverUrl: String?,
    val formats: Map<String, String>
)
