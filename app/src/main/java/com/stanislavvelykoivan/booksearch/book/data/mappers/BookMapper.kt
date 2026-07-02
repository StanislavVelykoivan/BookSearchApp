package com.stanislavvelykoivan.booksearch.book.data.mappers

import com.stanislavvelykoivan.booksearch.book.data.database.BookWithAuthors
import com.stanislavvelykoivan.booksearch.book.data.database.entitys.AuthorEntity
import com.stanislavvelykoivan.booksearch.book.data.database.entitys.BookEntity
import com.stanislavvelykoivan.booksearch.book.data.database.entitys.SearchHistoryEntity
import com.stanislavvelykoivan.booksearch.book.data.dto.BookSearchDto
import com.stanislavvelykoivan.booksearch.book.data.dto.BookSearchResponseDto
import com.stanislavvelykoivan.booksearch.book.data.dto.PersonDto
import com.stanislavvelykoivan.booksearch.book.domain.Author
import com.stanislavvelykoivan.booksearch.book.domain.Book
import com.stanislavvelykoivan.booksearch.book.domain.BookSearch

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


fun AuthorEntity.toAuthor(): Author {
    return Author(
        name = name,
        birthYear = birthYear,
        deathYear = deathYear
    )
}

fun Author.toAuthorEntity(): AuthorEntity {
    return AuthorEntity(
        authorId = 0,
        name = name,
        birthYear = birthYear,
        deathYear = deathYear
    )
}


fun Book.toBookEntity(): BookEntity {
    return BookEntity(
        bookId = id,
        title = title,
        languages = languages,
        subjects = subjects,
        bookshelves = bookshelves,
        downloadCount = downloadCount,
        coverUrl = coverUrl,
        formats = formats
    )
}
fun BookWithAuthors.toBook(): Book {
    return Book(
        id = book.bookId,
        title = book.title,
        authors = this.authors.map { it.toAuthor() },
        languages = book.languages,
        subjects = book.subjects,
        bookshelves = book.bookshelves,
        downloadCount = book.downloadCount,
        coverUrl = book.coverUrl,
        formats = book.formats
    )
}


fun SearchHistoryEntity.toDomain(): String = this.searchQuery

fun String.toEntity(): SearchHistoryEntity = SearchHistoryEntity(
    searchQuery = this,
    lastSearchedAt = System.currentTimeMillis()
)

fun BookSearchResponseDto.toBookSearch(): BookSearch{
    return BookSearch(
        count = count,
        next = next,
        previous = previous,
        results = results.map { it.toBook() }
    )
}