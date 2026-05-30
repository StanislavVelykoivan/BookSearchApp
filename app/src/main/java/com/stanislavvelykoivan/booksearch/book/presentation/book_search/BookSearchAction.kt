package com.stanislavvelykoivan.booksearch.book.presentation.book_search

import com.stanislavvelykoivan.booksearch.book.domain.Book

sealed interface BookSearchAction {

    data class OnQueryChange(val query: String) : BookSearchAction
    data object OnSearchClick : BookSearchAction

    data object OnLoadNextPage : BookSearchAction

    data class OnLanguageFilterClick(val languages: List<String>) : BookSearchAction

    data class OnTabSelected(val tabIndex: Int) : BookSearchAction

    data class OnBookClick(val bookId: Long) : BookSearchAction
}