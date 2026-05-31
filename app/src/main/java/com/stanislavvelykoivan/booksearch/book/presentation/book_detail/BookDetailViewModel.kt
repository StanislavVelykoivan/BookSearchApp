package com.stanislavvelykoivan.booksearch.book.presentation.book_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stanislavvelykoivan.booksearch.book.domain.BookRepository
import com.stanislavvelykoivan.booksearch.book.domain.DownloadBookUseCase
import com.stanislavvelykoivan.booksearch.core.domain.onError
import com.stanislavvelykoivan.booksearch.core.domain.onSuccess
import com.stanislavvelykoivan.booksearch.core.presentation.toUiText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

class BookDetailViewModel(
    private val bookRepository: BookRepository,
    private val downloadBookUseCase: DownloadBookUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(BookDetailState())
    val state = _state.asStateFlow()

    init {
        val bookId = savedStateHandle.get<Long>("bookId")
        if (bookId != null) {
            viewModelScope.launch {

                val savedBook = bookRepository.getBookFromDatabase(bookId)

                if (savedBook != null) {
                    _state.update { it.copy(book = savedBook, isLoading = false, isBookSaved = true) }
                } else {
                    loadBookFromNetwork(bookId)
                }
            }
        }
    }


    fun onAction(action: BookDetailAction) {
        when (action) {
            is BookDetailAction.LoadBook -> {

            }

            is BookDetailAction.DownloadFormat -> {
                val book = _state.value.book ?: return

                viewModelScope.launch {
                    _state.update { it.copy(isDownloading = true) }

                    val result = downloadBookUseCase(book, action.formatMimeType, action.url)

                    result.onSuccess {
                        _state.update { it.copy(isDownloading = false) }
                    }.onError { error ->
                        _state.update { it.copy(isDownloading = false, error = error.toUiText()) }
                    }
                }
            }
            is BookDetailAction.OnSaveClick -> {

            }

            is BookDetailAction.OnDeleteClick -> {

            }

            is BookDetailAction.OnRetryClick -> {
                viewModelScope.launch {
                    val bookId = _state.value.book?.id ?: return@launch

                    val cachedBook = bookRepository.getBookFromDatabase(bookId)

                    if (cachedBook != null) {
                        _state.update { it.copy(book = cachedBook, error = null) }
                    } else {
                        loadBookFromNetwork(bookId)
                    }
                }
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
