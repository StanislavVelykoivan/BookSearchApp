package com.stanislavvelykoivan.booksearch.book.presentation.book_search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stanislavvelykoivan.booksearch.book.domain.BookRepository
import com.stanislavvelykoivan.booksearch.core.domain.onError
import com.stanislavvelykoivan.booksearch.core.domain.onSuccess
import com.stanislavvelykoivan.booksearch.core.presentation.toUiText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BookSearchViewModel(
    private val bookRepository: BookRepository
) : ViewModel() {
    private val _state = MutableStateFlow(BookSearchState())

    val state = _state.asStateFlow()

    private var searchJob: Job? = null

    init {
        observeSavedBooks()
        observeSearchHistory()
    }

    fun onAction(action: BookSearchAction) {
        when (action) {
            is BookSearchAction.OnQueryChange -> {
                _state.update { it.copy(query = action.query) }
            }

            is BookSearchAction.OnSearchClick -> {
                searchBooks(
                    query = _state.value.query,
                    languages = _state.value.selectedLanguages
                )
            }

            is BookSearchAction.OnLanguageFilterClick -> {
                _state.update { it.copy(selectedLanguages = action.languages) }
            }

            is BookSearchAction.OnTabSelected -> {
                _state.update { it.copy(onTabSelected = action.tabIndex) }
            }

            is BookSearchAction.OnLoadNextPage -> {
                if (_state.value.isLoading || _state.value.next.isNullOrEmpty()) return
                _state.update { it.copy(onNextLoading = true) }
                nextBooks(_state.value.next!!)

            }

            is BookSearchAction.OnBookClick -> {}


        }
    }

    private fun nextBooks(query: String) {
        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            bookRepository.nexBooks(query)
                .onSuccess { nextBooks ->
                    _state.update {
                        it.copy(
                            next = nextBooks.next,
                            error = null,
                            onNextLoading = false,
                            searchResult = _state.value.searchResult + nextBooks.results
                        )
                    }
                }
                .onError {
                    _state.update {
                        it.copy(
                            onNextLoading = false,
                        )
                    }
                }
        }
    }


    private fun searchBooks(query: String, languages: List<String>? = null, page: Int = 1) {
        val trimmedQuery = query.trim()
        if (trimmedQuery.length < 2) {
            return
        }

        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            _state.update { it.copy(error = null, isLoading = true) }

            launch {
                bookRepository.saveSearchQuery(trimmedQuery)
            }

            bookRepository.searchBooks(trimmedQuery, languages, page)
                .onSuccess { searchResult ->
                    _state.update {
                        it.copy(
                            next = searchResult.next,
                            isLoading = false,
                            error = null,
                            onNextLoading = false,
                            searchResult = searchResult.results,
                        )
                    }
                }
                .onError { error ->
                    _state.update {
                        it.copy(
                            onNextLoading = false,
                            isLoading = false,
                            error = error.toUiText()
                        )
                    }
                }
        }
    }

    private fun observeSavedBooks() {
        viewModelScope.launch {
            bookRepository.getSavedBooks().collect { savedBooks ->
                _state.update { it.copy(savedBooks = savedBooks) }
            }
        }
    }

    private fun observeSearchHistory() {
        viewModelScope.launch {
            bookRepository.getLastSearchQuery().collect { history ->
                _state.update { it.copy(searchHistory = history) }
            }
        }
    }

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun observeSearch() {
        _state
            .map { it.query.trim() }
            .distinctUntilChanged()
            .debounce(700)
            .filter { it.length >= 2 }
            .flatMapLatest { query ->
                flow {
                    _state.update {
                        it.copy(
                            error = null,
                            isLoading = true
                        )
                    }
                    

                    val result = bookRepository.searchBooks(
                        query = query,
                        languages = _state.value.selectedLanguages
                    )
                    emit(result)
                }
            }
            .onEach { result ->
                result
                    .onSuccess { books ->
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = null,
                                searchResult = books.results
                            )
                        }
                    }
                    .onError { error ->
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = error.toUiText()
                            )
                        }
                    }
            }
            .launchIn(viewModelScope)
    }
}

