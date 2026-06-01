package com.stanislavvelykoivan.booksearch.book.domain

import com.stanislavvelykoivan.booksearch.core.domain.DataError
import com.stanislavvelykoivan.booksearch.core.domain.Result


class DeleteBookUseCase(
    private val bookRepository: BookRepository
) {
    suspend operator fun invoke(bookId: Long): Result<Unit, DataError.Local> {

        bookRepository.deleteBook(bookId)

        bookRepository.deleteBookFromDatabase(bookId)

        return Result.Success(Unit)
    }
}