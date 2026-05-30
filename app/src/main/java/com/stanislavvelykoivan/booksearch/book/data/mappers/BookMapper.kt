package com.stanislavvelykoivan.booksearch.book.data.mappers

import com.stanislavvelykoivan.booksearch.book.data.dto.BookSearchDto
import com.stanislavvelykoivan.booksearch.book.data.dto.PersonDto
import com.stanislavvelykoivan.booksearch.book.domain.Author
import com.stanislavvelykoivan.booksearch.book.domain.Book

fun BookSearchDto.toBook(): Book {
    return Book(
        id = id,
        title = title,
        authors = authors.map { it.toAuthor() },
        languages = languages,
        subjects = subjects,
        bookshelves = bookshelves,
        downloadCount = downloadCount,
        coverUrl = formats["image/jpeg"],
        formats = formats
    )
}
fun PersonDto.toAuthor(): Author {
    return Author(
        name = name ?: "Unknown Author",
        birthYear = birthYear,
        deathYear = deathYear
    )
}
