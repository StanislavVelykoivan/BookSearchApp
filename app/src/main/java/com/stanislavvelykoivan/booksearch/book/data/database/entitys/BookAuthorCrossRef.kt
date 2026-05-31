package com.stanislavvelykoivan.booksearch.book.data.database.entitys

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "book_author_cross_ref",
    primaryKeys = ["bookId", "authorId"],
    indices = [Index(value = ["authorId"])]
)
data class BookAuthorCrossRef(
    val bookId: Long,
    val authorId: Long
)