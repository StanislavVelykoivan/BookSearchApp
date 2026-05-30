package com.stanislavvelykoivan.booksearch.book.presentation.book_search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stanislavvelykoivan.booksearch.book.domain.BookRepository
import com.stanislavvelykoivan.booksearch.core.domain.onError
import com.stanislavvelykoivan.booksearch.core.domain.onSuccess
import com.stanislavvelykoivan.booksearch.core.presentation.toUiText
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BookSearchViewModel(
    private val bookRepository: BookRepository
) : ViewModel() {
    private val _state = MutableStateFlow(BookSearchState())

    val state = _state.asStateFlow()

    private var searchJob: Job? = null

    fun onAction(action: BookSearchAction) {
        when (action) {
            is BookSearchAction.OnQueryChange -> {
                _state.update { it.copy(query = action.query, page = 1) }
            }

            is BookSearchAction.OnSearchClick -> {
                _state.update { it.copy(page = 1) }
                searchBooks(
                    query = _state.value.query,
                    languages = _state.value.selectedLanguages,
                    page = _state.value.page
                )
            }

            is BookSearchAction.OnLanguageFilterClick -> {
                _state.update { it.copy(selectedLanguages = action.languages, page = 1) }
            }

            is BookSearchAction.OnTabSelected -> {
                _state.update { it.copy(onTabSelected = action.tabIndex) }
            }

            is BookSearchAction.OnLoadNextPage -> {
                _state.update { it.copy(page = it.page + 1) }
            }

            is BookSearchAction.OnBookClick -> {}


        }
    }


    private fun searchBooks(query: String, languages: List<String>? = null, page: Int = 1) {
        val trimmedQuery = query.trim()
        if (trimmedQuery.length < 2) {
            return
        }

        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            bookRepository.searchBooks(trimmedQuery, languages, page)
                .onSuccess { searchResult ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = null,
                            searchResult = if (page == 1) searchResult else _state.value.searchResult + searchResult,
                        )
                    }
                }
                .onError { error ->
                    _state.update {
                        it.copy(
                            searchResult = emptyList(),
                            isLoading = false,
                            error = error.toUiText()
                        )
                    }
                }
        }
    }
}

