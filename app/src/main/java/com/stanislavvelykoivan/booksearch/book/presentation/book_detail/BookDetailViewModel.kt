package com.stanislavvelykoivan.booksearch.book.presentation.book_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stanislavvelykoivan.booksearch.book.domain.BookRepository
import com.stanislavvelykoivan.booksearch.core.domain.onError
import com.stanislavvelykoivan.booksearch.core.domain.onSuccess
import com.stanislavvelykoivan.booksearch.core.presentation.toUiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BookDetailViewModel(
    private val bookRepository: BookRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(BookDetailState())
    val state = _state.asStateFlow()

    init {

        val bookId = savedStateHandle.get<Long>("bookId")
        if (bookId != null) {
            loadBookFromNetwork(bookId)
        }
    }



    fun onAction(action: BookDetailAction) {
        when (action) {
            is BookDetailAction.LoadBook -> {

            }

            is BookDetailAction.OnSaveClick -> {

            }

            is BookDetailAction.OnDeleteClick -> {

            }

            is BookDetailAction.OnRetryClick -> {

            }
        }
    }

    private fun loadBookFromNetwork(bookId: Long) {
        viewModelScope.launch {
        _state.update { it.copy(isLoading = true) }
            bookRepository.getBookById(bookId)
                .onSuccess { book ->
                    _state.value = _state.value.copy(book = book , isLoading = false)
                }
                .onError { error ->
                    _state.value = _state.value.copy(error = error.toUiText(), isLoading = false)
                }
        }
    }
}
