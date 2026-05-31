package com.stanislavvelykoivan.booksearch.book.domain


data class Book(
    val id: Long,
    val title: String,
    val authors: List<Author>,
    val languages: List<String>,
    val subjects: List<String>,
    val bookshelves: List<String>,
    val downloadCount: Int,
    val coverUrl: String?,
    val formats: Map<String, String>
)
