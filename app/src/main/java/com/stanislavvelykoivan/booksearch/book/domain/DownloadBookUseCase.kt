package com.stanislavvelykoivan.booksearch.book.domain

import com.stanislavvelykoivan.booksearch.core.domain.DataError
import com.stanislavvelykoivan.booksearch.core.domain.Result


class DownloadBookUseCase(
    private val repository: BookRepository
) {
    suspend operator fun invoke(
        book: Book,
        formatMimeType: String,
        url: String
    ): Result<String, DataError> {

        repository.saveBookToDatabase(book)
        return repository.downloadFormat(book.id, formatMimeType, url)
    }
}