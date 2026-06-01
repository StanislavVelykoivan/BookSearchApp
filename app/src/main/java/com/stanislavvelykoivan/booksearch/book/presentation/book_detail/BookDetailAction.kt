package com.stanislavvelykoivan.booksearch.book.presentation.book_detail

import com.stanislavvelykoivan.booksearch.book.domain.Book
import java.io.File

sealed interface BookDetailAction {
    data class LoadBook(val bookId: Long): BookDetailAction

    data class DownloadFormat(val formatMimeType: String, val url: String) : BookDetailAction
    data class OnSaveClick(val book: Book, val formatUrl: String, val formatType: String): BookDetailAction
    data class OpenFile(val file: File): BookDetailAction
    object OnDeleteClick : BookDetailAction
    object OnRetryClick : BookDetailAction
}