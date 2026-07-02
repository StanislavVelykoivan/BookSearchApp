package com.stanislavvelykoivan.booksearch.book.domain

data class BookSearch(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<Book>
)
