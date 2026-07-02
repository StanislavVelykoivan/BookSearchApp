package com.stanislavvelykoivan.booksearch.book.domain

import com.stanislavvelykoivan.booksearch.core.domain.DataError
import com.stanislavvelykoivan.booksearch.core.domain.Result


class DeleteBookUseCase(
    private val bookRepository: BookRepository
) {
    suspend operator fun invoke(bookId: Long): Result<Unit, DataError.Local> {

        val resultFile = bookRepository.deleteBook(bookId)

        if (resultFile is Result.Error) return resultFile


        val resultDb = bookRepository.deleteBookFromDatabase(bookId)

        if (resultDb is Result.Error) return resultDb

        return Result.Success(Unit)
    }
}