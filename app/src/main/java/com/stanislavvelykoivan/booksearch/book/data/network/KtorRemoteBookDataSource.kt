package com.stanislavvelykoivan.booksearch.book.data.network

import com.stanislavvelykoivan.booksearch.book.data.dto.BookSearchDto
import com.stanislavvelykoivan.booksearch.book.data.dto.BookSearchResponseDto
import com.stanislavvelykoivan.booksearch.core.data.safeCall
import com.stanislavvelykoivan.booksearch.core.domain.DataError
import com.stanislavvelykoivan.booksearch.core.domain.Result
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter

private const val BASE_URL = "https://gutendex.com"

class KtorRemoteBookDataSource(
    private val httpClient: HttpClient
) : RemoteBookDataSource {

    override suspend fun searchBooks(
        query: String,
        languages: List<String>?,
        page: Int
    ): Result<BookSearchResponseDto, DataError.Remote> {
        return safeCall<BookSearchResponseDto> {
            httpClient.get(
                "$BASE_URL/books"
            ) {
                parameter("search", query)
                if (!languages.isNullOrEmpty()) {
                    parameter("languages", languages.joinToString(","))
                }
                parameter("page", page)
            }
        }
    }

    override suspend fun getBookById(bookId: Long): Result<BookSearchDto, DataError.Remote> {
        return safeCall<BookSearchDto> {
            httpClient.get(
                "$BASE_URL/books/$bookId"
            )
        }
    }


}
