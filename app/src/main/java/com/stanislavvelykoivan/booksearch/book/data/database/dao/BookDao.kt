package com.stanislavvelykoivan.booksearch.book.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.stanislavvelykoivan.booksearch.book.data.database.BookWithAuthors
import com.stanislavvelykoivan.booksearch.book.data.database.entitys.AuthorEntity
import com.stanislavvelykoivan.booksearch.book.data.database.entitys.BookAuthorCrossRef
import com.stanislavvelykoivan.booksearch.book.data.database.entitys.BookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBooks(books: List<BookEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAuthors(authors: List<AuthorEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrossRef(crossRef: List<BookAuthorCrossRef>)

    @Query("SELECT EXISTS(SELECT 1 FROM books WHERE bookId = :bookId)")
    fun isBookSaved(bookId: Long): Flow<Boolean>

    @Query("SELECT authorId FROM authors WHERE name = :name")
    fun getAuthorIdByName(name: String): Long?

    @Transaction
    @Query("SELECT * FROM books")
    fun getSavedBooks(): Flow<List<BookWithAuthors>>

    @Transaction
    @Query("SELECT * FROM books WHERE bookId = :bookId")
    suspend fun getBookWithAuthorsById(bookId: Long): BookWithAuthors?

    @Transaction
    suspend fun saveEverything(book: BookEntity, authors: List<AuthorEntity>) {
        insertBooks(listOf(book))

        insertAuthors(authors)

        val crossRefs = authors.mapNotNull { author ->
            val authorId = getAuthorIdByName(author.name)
            authorId?.let { id -> BookAuthorCrossRef(book.bookId, id) }
        }

        if (crossRefs.isNotEmpty()) {
            insertCrossRef(crossRefs)
        }
    }
}