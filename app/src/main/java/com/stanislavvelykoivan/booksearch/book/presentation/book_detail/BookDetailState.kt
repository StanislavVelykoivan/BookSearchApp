package com.stanislavvelykoivan.booksearch.book.presentation.book_detail

import com.stanislavvelykoivan.booksearch.book.domain.Book
import com.stanislavvelykoivan.booksearch.core.presentation.UiText

data class BookDetailState(
    val book: Book? = null,
    val isDownloading: Boolean = false,
    val isLoading: Boolean = false,
    val error: UiText? = null,
    val fileSize: String? = null,
    val isBookSaved: Boolean = false
)
