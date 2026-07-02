package com.stanislavvelykoivan.booksearch.book.presentation.book_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stanislavvelykoivan.booksearch.book.domain.BookRepository
import com.stanislavvelykoivan.booksearch.book.domain.DeleteBookUseCase
import com.stanislavvelykoivan.booksearch.book.domain.DownloadBookUseCase
import com.stanislavvelykoivan.booksearch.core.domain.onError
import com.stanislavvelykoivan.booksearch.core.domain.onSuccess
import com.stanislavvelykoivan.booksearch.core.presentation.toUiText
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BookDetailViewModel(
    private val bookRepository: BookRepository,
    private val downloadBookUseCase: DownloadBookUseCase,
    private val deleteBookUseCase: DeleteBookUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(BookDetailState())
    val state = _state.asStateFlow()

    private val _events = MutableSharedFlow<BookDetailEvent>()
    val event = _events.asSharedFlow()

    init {
        val bookId = savedStateHandle.get<Long>("bookId")
        if (bookId != null) {
            loadBook(bookId)
        }
    }


    fun onAction(action: BookDetailAction) {
        when (action) {
            is BookDetailAction.LoadBook -> {
                loadBook(action.bookId)
            }

            is BookDetailAction.DownloadFormat -> {
                val book = _state.value.book ?: return

                viewModelScope.launch {
                    _state.update { it.copy(isDownloading = true, progression = 0f) }

                    val result = downloadBookUseCase(
                        book,
                        action.formatMimeType,
                        action.url,
                        onProgress = { progress ->
                            _state.update { it.copy(progression = progress) }
                        })

                    result.onSuccess {
                        val getBookFiles = bookRepository.getBookFiles(book.id)
                        _state.update {
                            it.copy(
                                isDownloading = false,
                                files = getBookFiles,
                                isBookSaved = true
                            )
                        }
                    }.onError { error ->
                        _state.update { it.copy(isDownloading = false, error = error.toUiText()) }
                    }
                }
            }

            is BookDetailAction.OpenFile -> {
                viewModelScope.launch {
                    bookRepository.openFile(action.file)
                        .onError { error ->
                            _events.emit(
                                BookDetailEvent.ShowError(error.toUiText())
                            )
                        }
                }
            }

            is BookDetailAction.OnSaveClick -> {

            }

            is BookDetailAction.OnDeleteClick -> {
                val book = _state.value.book ?: return

                viewModelScope.launch {
                    _state.update { it.copy(isLoading = true) }

                    deleteBookUseCase(book.id)
                        .onSuccess {
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    isBookSaved = false,
                                    files = emptyList()
                                )
                            }
                        }
                        .onError { error ->
                            _state.update { it.copy(isLoading = false, error = error.toUiText()) }
                        }
                }
            }

            is BookDetailAction.OnRetryClick -> {
                _state.value.book?.id?.let { bookId ->
                    loadBook(bookId)
                }
            }
        }
    }

    private fun loadBook(bookId: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val cachedBook = bookRepository.getBookFromDatabase(bookId)

            if (cachedBook != null) {
                val getBookFiles = bookRepository.getBookFiles(bookId)


                _state.update {
                    it.copy(
                        book = cachedBook,
                        files = getBookFiles,
                        isLoading = false,
                        isBookSaved = true,
                        error = null
                    )
                }
            } else {
                loadBookFromNetwork(bookId)
            }
        }
    }

    private fun loadBookFromNetwork(bookId: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            bookRepository.getBookById(bookId)
                .onSuccess { book ->
                    _state.update { it.copy(book = book, isLoading = false) }
                }
                .onError { error ->
                    _state.update { it.copy(error = error.toUiText(), isLoading = false) }
                }
        }
    }


}
