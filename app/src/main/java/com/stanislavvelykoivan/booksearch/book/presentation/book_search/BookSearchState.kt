package com.stanislavvelykoivan.booksearch.book.presentation.book_search

import com.stanislavvelykoivan.booksearch.book.domain.Book
import com.stanislavvelykoivan.booksearch.core.presentation.UiText

data class BookSearchState (
    val query: String = "tolstoy",
    val searchResult: List<Book> = emptyList(),
    val searchHistory: List<String> = emptyList(),
    val savedBooks: List<Book> = emptyList(),
    val isLoading: Boolean = false,
    val error: UiText? = null,
    val onTabSelected: Int = 0,
    val page: Int = 1,
    val isEndOfList: Boolean = false,
    val selectedLanguages: List<String> = emptyList()
)