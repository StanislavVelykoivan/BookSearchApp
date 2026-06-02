package com.stanislavvelykoivan.booksearch

import com.google.common.truth.Truth.assertThat
import com.stanislavvelykoivan.booksearch.book.data.database.BookWithAuthors
import com.stanislavvelykoivan.booksearch.book.data.database.entitys.AuthorEntity
import com.stanislavvelykoivan.booksearch.book.data.database.entitys.BookEntity
import com.stanislavvelykoivan.booksearch.book.data.database.entitys.SearchHistoryEntity
import com.stanislavvelykoivan.booksearch.book.data.dto.BookSearchDto
import com.stanislavvelykoivan.booksearch.book.data.dto.PersonDto
import com.stanislavvelykoivan.booksearch.book.data.mappers.toAuthor
import com.stanislavvelykoivan.booksearch.book.data.mappers.toAuthorEntity
import com.stanislavvelykoivan.booksearch.book.data.mappers.toBook
import com.stanislavvelykoivan.booksearch.book.data.mappers.toBookEntity
import com.stanislavvelykoivan.booksearch.book.data.mappers.toDomain
import com.stanislavvelykoivan.booksearch.book.data.mappers.toEntity
import com.stanislavvelykoivan.booksearch.book.domain.Author
import org.junit.Test

class BookMapperTest {

    @Test
    fun `BookSearchDto toBook maps correctly with all fields`() {
        val formats = mapOf(
            "image/jpeg" to "https://test.com/cover.jpg",
            "text/html" to "https://test.com/book.html"
        )
        val dto = BookSearchDto(
            id = 123,
            title = "Master and Margarita",
            authors = listOf(
                PersonDto(name = "Mikhail Bulgakov", birthYear = 1891, deathYear = 1940)
            ),
            languages = listOf("ru"),
            subjects = listOf("Classic", "Magic Realism"),
            bookshelves = listOf("Top", "Russian Classics"),
            downloadCount = 5000,
            formats = formats
        )

        val domainBook = dto.toBook()

        assertThat(domainBook.id).isEqualTo(123)
        assertThat(domainBook.title).isEqualTo("Master and Margarita")
        assertThat(domainBook.authors).hasSize(1)
        assertThat(domainBook.authors.first().name).isEqualTo("Mikhail Bulgakov")
        assertThat(domainBook.authors.first().birthYear).isEqualTo(1891)
        assertThat(domainBook.authors.first().deathYear).isEqualTo(1940)
        assertThat(domainBook.languages).containsExactly("ru")
        assertThat(domainBook.subjects).containsExactly("Classic", "Magic Realism")
        assertThat(domainBook.bookshelves).containsExactly("Top", "Russian Classics")
        assertThat(domainBook.downloadCount).isEqualTo(5000)
        assertThat(domainBook.coverUrl).isEqualTo("https://test.com/cover.jpg")
        assertThat(domainBook.formats).isEqualTo(formats)
    }

    @Test
    fun `PersonDto toAuthor handles null name`() {
        val personDto = PersonDto(name = "tolstoy", birthYear = null, deathYear = 1850)

        val author = personDto.toAuthor()

        assertThat(author.name).isEqualTo("tolstoy")
        assertThat(author.birthYear).isNull()
        assertThat(author.deathYear).isEqualTo(1850)
    }

    @Test
    fun `String toEntity for search history sets timestamp`() {
        val query = "Android Development"

        val entity = query.toEntity()

        assertThat(entity.searchQuery).isEqualTo("Android Development")
        assertThat(entity.lastSearchedAt).isGreaterThan(0L)
    }

    @Test
    fun `SearchHistoryEntity to domain String maps correctly`() {
        val entity = SearchHistoryEntity(
            searchQuery = "Clean Architecture",
            lastSearchedAt = 123456789L
        )

        val domain = entity.toDomain()

        assertThat(domain).isEqualTo("Clean Architecture")
    }

    @Test
    fun `Book toBookEntity maps all fields correctly`() {
        val formats = mapOf("application/epub+zip" to "https://test.com/book.epub")
        val book = com.stanislavvelykoivan.booksearch.book.domain.Book(
            id = 456,
            title = "Kotlin for Beginners",
            authors = emptyList(),
            languages = listOf("en", "ua"),
            subjects = listOf("Programming"),
            bookshelves = listOf("Tech"),
            downloadCount = 10,
            coverUrl = "https://test.com/kotlin.png",
            formats = formats
        )

        val entity = book.toBookEntity()

        assertThat(entity.bookId).isEqualTo(456)
        assertThat(entity.title).isEqualTo("Kotlin for Beginners")
        assertThat(entity.languages).containsExactly("en", "ua")
        assertThat(entity.subjects).containsExactly("Programming")
        assertThat(entity.bookshelves).containsExactly("Tech")
        assertThat(entity.downloadCount).isEqualTo(10)
        assertThat(entity.coverUrl).isEqualTo("https://test.com/kotlin.png")
        assertThat(entity.formats).isEqualTo(formats)
    }

    @Test
    fun `BookWithAuthors to domain Book maps correctly`() {
        val bookEntity = BookEntity(
            bookId = 789,
            title = "Effective Kotlin",
            languages = listOf("en"),
            subjects = listOf("Development"),
            bookshelves = emptyList(),
            downloadCount = 100,
            coverUrl = "url",
            formats = emptyMap()
        )
        val authorEntities = listOf(
            AuthorEntity(authorId = 1, name = "Marcin Moskala", birthYear = null, deathYear = null)
        )

        val bookWithAuthors = BookWithAuthors(
            book = bookEntity,
            authors = authorEntities
        )

        val domain = bookWithAuthors.toBook()

        assertThat(domain.id).isEqualTo(789)
        assertThat(domain.title).isEqualTo("Effective Kotlin")
        assertThat(domain.authors).hasSize(1)
        assertThat(domain.authors.first().name).isEqualTo("Marcin Moskala")
        assertThat(domain.languages).containsExactly("en")
    }

    @Test
    fun `AuthorEntity to domain Author maps correctly`() {
        val entity = AuthorEntity(
            authorId = 123,
            name = "George Orwell",
            birthYear = 1903,
            deathYear = 1950
        )

        val domain = entity.toAuthor()

        assertThat(domain.name).isEqualTo("George Orwell")
        assertThat(domain.birthYear).isEqualTo(1903)
        assertThat(domain.deathYear).isEqualTo(1950)
    }

    @Test
    fun `domain Author to AuthorEntity maps correctly`() {
        val domain = Author(
            name = "H.P. Lovecraft",
            birthYear = 1890,
            deathYear = 1937
        )

        val entity = domain.toAuthorEntity()

        assertThat(entity.authorId).isEqualTo(0)
        assertThat(entity.name).isEqualTo("H.P. Lovecraft")
        assertThat(entity.birthYear).isEqualTo(1890)
        assertThat(entity.deathYear).isEqualTo(1937)
    }
}