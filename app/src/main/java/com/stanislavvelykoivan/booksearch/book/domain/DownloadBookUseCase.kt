package com.stanislavvelykoivan.booksearch.book.domain

import com.stanislavvelykoivan.booksearch.core.data.retry
import com.stanislavvelykoivan.booksearch.core.domain.DataError
import com.stanislavvelykoivan.booksearch.core.domain.Result


class DownloadBookUseCase(
    private val repository: BookRepository
) {
    suspend operator fun invoke(
        book: Book,
        formatMimeType: String,
        url: String,
        onProgress: (Float) -> Unit
    ): Result<String, DataError> {
        val dbResult = repository.saveBookToDatabase(book)
        if (dbResult is Result.Error)
            return dbResult

        val formatResult = retry {
            repository.downloadFormat(
                bookId = book.id,
                formatMimeType = formatMimeType,
                url = url,
                onProgress = onProgress
            )
        }

        if (formatResult is Result.Error){
            repository.deleteBookFromDatabase(book.id)
            return formatResult
        }
        return formatResult
    }
}