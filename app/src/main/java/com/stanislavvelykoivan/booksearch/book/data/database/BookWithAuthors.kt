package com.stanislavvelykoivan.booksearch.book.data.database

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.stanislavvelykoivan.booksearch.book.data.database.entitys.AuthorEntity
import com.stanislavvelykoivan.booksearch.book.data.database.entitys.BookAuthorCrossRef
import com.stanislavvelykoivan.booksearch.book.data.database.entitys.BookEntity

data class BookWithAuthors(
    @Embedded val book: BookEntity,
    @Relation(
        parentColumn = "bookId",
        entityColumn = "authorId",
        associateBy = Junction(BookAuthorCrossRef::class)
    )
    val authors: List<AuthorEntity>
)