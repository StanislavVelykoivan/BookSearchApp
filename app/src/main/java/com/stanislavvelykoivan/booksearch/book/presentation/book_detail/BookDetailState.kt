package com.stanislavvelykoivan.booksearch.book.presentation.book_detail

import com.stanislavvelykoivan.booksearch.book.domain.Book
import com.stanislavvelykoivan.booksearch.book.domain.BookFile
import com.stanislavvelykoivan.booksearch.core.presentation.UiText

data class BookDetailState(
    val book: Book? = null,
    val isDownloading: Boolean = false,
    val isLoading: Boolean = false,
    val error: UiText? = null,
    val progression: Float = 0f,
    val files: List<BookFile> = emptyList(),
    val isBookSaved: Boolean = false
)
