package com.stanislavvelykoivan.booksearch.book.data.repository

import com.stanislavvelykoivan.booksearch.book.data.mappers.toBook
import com.stanislavvelykoivan.booksearch.book.data.network.RemoteBookDataSource
import com.stanislavvelykoivan.booksearch.book.domain.Book
import com.stanislavvelykoivan.booksearch.book.domain.BookRepository
import com.stanislavvelykoivan.booksearch.core.domain.DataError
import com.stanislavvelykoivan.booksearch.core.domain.Result
import com.stanislavvelykoivan.booksearch.core.domain.map
import kotlinx.coroutines.flow.Flow

class DefaultBookRepository(
    private val remoteBookDataSource: RemoteBookDataSource
): BookRepository {
    override suspend fun searchBooks(
        query: String,
        languages: List<String>?,
        page: Int
    ): Result<List<Book>, DataError.Remote> {
        return remoteBookDataSource
            .searchBooks(query, languages, page)
            .map { dto ->
                dto.results.map {
                    it.toBook()
                }
            }
    }

    override suspend fun getBookById(bookId: Long): Result<Book, DataError.Remote> {
        return remoteBookDataSource
            .getBookById(bookId)
            .map { dto ->
                dto.toBook()
            }
    }

    override fun isBookSaved(bookId: Long): Flow<Boolean> {
        TODO("Not yet implemented")
    }


}