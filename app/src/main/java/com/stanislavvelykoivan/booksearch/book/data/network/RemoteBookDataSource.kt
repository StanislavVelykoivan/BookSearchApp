package com.stanislavvelykoivan.booksearch.book.data.network

import com.stanislavvelykoivan.booksearch.book.data.dto.BookSearchDto
import com.stanislavvelykoivan.booksearch.book.data.dto.BookSearchResponseDto
import com.stanislavvelykoivan.booksearch.core.domain.DataError
import com.stanislavvelykoivan.booksearch.core.domain.Result
import io.ktor.utils.io.ByteReadChannel

interface RemoteBookDataSource {
    suspend fun searchBooks(
        query: String,
        languages: List<String>? = null,
        page: Int = 1
    ): Result<BookSearchResponseDto, DataError.Remote>

    suspend fun getBookById(bookId: Long): Result<BookSearchDto, DataError.Remote>

    suspend fun downloadStreaming(
        url: String,
        onChannelReady: suspend (ByteReadChannel) -> Result<Unit, DataError.Local>
    ): Result<Unit, DataError>
}