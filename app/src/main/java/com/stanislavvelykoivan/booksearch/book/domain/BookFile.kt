package com.stanislavvelykoivan.booksearch.book.domain

import java.io.File

data class BookFile(
    val name: String,
    val format: String,
    val size: String,
    val file: File
)
