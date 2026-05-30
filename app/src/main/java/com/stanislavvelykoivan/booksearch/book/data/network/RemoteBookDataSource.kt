package com.stanislavvelykoivan.booksearch.book.data.network

import com.stanislavvelykoivan.booksearch.book.data.dto.BookSearchDto
import com.stanislavvelykoivan.booksearch.book.data.dto.BookSearchResponseDto
import com.stanislavvelykoivan.booksearch.book.domain.Book
import com.stanislavvelykoivan.booksearch.core.domain.DataError
import com.stanislavvelykoivan.booksearch.core.domain.Result
interface RemoteBookDataSource {
    suspend fun searchBooks(
        query: String,
        languages: List<String>? = null,
        page: Int = 1
    ): Result<BookSearchResponseDto, DataError.Remote>

    suspend fun getBookById(bookId: Long): Result<BookSearchDto, DataError.Remote>
}