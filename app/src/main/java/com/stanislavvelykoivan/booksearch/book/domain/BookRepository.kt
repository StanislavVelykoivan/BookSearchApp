package com.stanislavvelykoivan.booksearch.book.domain

import com.stanislavvelykoivan.booksearch.core.domain.DataError
import com.stanislavvelykoivan.booksearch.core.domain.Result
import kotlinx.coroutines.flow.Flow
import java.io.File

interface BookRepository {
    suspend fun searchBooks(
        query: String,
        languages: List<String>? = null,
        page: Int = 1
    ): Result<List<Book>, DataError.Remote>

    suspend fun getBookById(bookId: Long): Result<Book, DataError.Remote>
    fun isBookSaved(bookId: Long): Flow<Boolean>

    suspend fun saveBookToDatabase(book: Book)

    fun getSavedBooks(): Flow<List<Book>>
    suspend fun downloadFormat(
        bookId: Long,
        formatMimeType: String,
        url: String
    ): Result<String, DataError>

    suspend fun getBookFromDatabase(bookId: Long): Book?
    suspend fun getBookFiles(bookId: Long): List<BookFile>
    suspend fun openFile(file: File): Result<Unit, DataError.Local>
    suspend fun deleteBookFromDatabase(bookId: Long)
    suspend fun deleteBook(bookId: Long): Boolean

    fun getLastSearchQuery(): Flow<List<String>>
    suspend fun saveSearchQuery(query: String)
}