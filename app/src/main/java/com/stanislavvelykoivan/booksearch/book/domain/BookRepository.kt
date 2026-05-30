package com.stanislavvelykoivan.booksearch.book.domain

import com.stanislavvelykoivan.booksearch.core.domain.DataError
import com.stanislavvelykoivan.booksearch.core.domain.Result
import kotlinx.coroutines.flow.Flow

interface BookRepository {
    suspend fun searchBooks(query: String, languages: List<String>? = null, page: Int = 1): Result<List<Book>, DataError.Remote>
    suspend fun getBookById(bookId: Long): Result<Book, DataError.Remote>
    fun isBookSaved(bookId: Long): Flow<Boolean>

}