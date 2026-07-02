package com.stanislavvelykoivan.booksearch.book.presentation.book_detail

import com.stanislavvelykoivan.booksearch.core.presentation.UiText

sealed interface BookDetailEvent {
    data class ShowError(val message: UiText) : BookDetailEvent
    data class ShowMessage(val message: UiText) : BookDetailEvent
}