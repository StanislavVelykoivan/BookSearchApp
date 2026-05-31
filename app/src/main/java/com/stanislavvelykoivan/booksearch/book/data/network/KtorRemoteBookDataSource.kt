package com.stanislavvelykoivan.booksearch.book.data.network

import com.stanislavvelykoivan.booksearch.book.data.dto.BookSearchDto
import com.stanislavvelykoivan.booksearch.book.data.dto.BookSearchResponseDto
import com.stanislavvelykoivan.booksearch.core.data.safeCall
import com.stanislavvelykoivan.booksearch.core.domain.DataError
import com.stanislavvelykoivan.booksearch.core.domain.Result
import io.ktor.client.HttpClient
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.prepareGet
import io.ktor.http.ContentType
import io.ktor.utils.io.ByteReadChannel

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
                accept(ContentType.Application.Json)
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
            ){
                accept(ContentType.Application.Json)
            }
        }
    }

    override suspend fun downloadBookChannel(url: String): Result<ByteReadChannel, DataError.Remote> {
        return safeCall<ByteReadChannel> {
            val response = httpClient.prepareGet(url).execute()
            response
        }
    }

}
