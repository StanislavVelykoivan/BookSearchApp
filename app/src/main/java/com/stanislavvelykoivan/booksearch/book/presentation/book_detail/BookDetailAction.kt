package com.stanislavvelykoivan.booksearch.book.presentation.book_detail

import com.stanislavvelykoivan.booksearch.book.domain.Book

sealed interface BookDetailAction {
    data class LoadBook(val bookId: Long): BookDetailAction

    data class DownloadFormat(val formatMimeType: String, val url: String) : BookDetailAction
    data class OnSaveClick(val book: Book, val formatUrl: String, val formatType: String): BookDetailAction
    object OnDeleteClick : BookDetailAction
    object OnRetryClick : BookDetailAction
}