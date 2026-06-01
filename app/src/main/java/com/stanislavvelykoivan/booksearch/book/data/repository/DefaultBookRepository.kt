package com.stanislavvelykoivan.booksearch.book.data.repository

import android.util.Log
import com.stanislavvelykoivan.booksearch.book.data.database.dao.BookDao
import com.stanislavvelykoivan.booksearch.book.data.database.dao.SearchDao
import com.stanislavvelykoivan.booksearch.book.data.database.entitys.SearchHistoryEntity
import com.stanislavvelykoivan.booksearch.book.data.files.FileStorage
import com.stanislavvelykoivan.booksearch.book.data.mappers.toAuthorEntity
import com.stanislavvelykoivan.booksearch.book.data.mappers.toBook
import com.stanislavvelykoivan.booksearch.book.data.mappers.toBookEntity
import com.stanislavvelykoivan.booksearch.book.data.mappers.toDomain
import com.stanislavvelykoivan.booksearch.book.data.network.RemoteBookDataSource
import com.stanislavvelykoivan.booksearch.book.domain.Book
import com.stanislavvelykoivan.booksearch.book.domain.BookFile
import com.stanislavvelykoivan.booksearch.book.domain.BookRepository
import com.stanislavvelykoivan.booksearch.core.domain.DataError
import com.stanislavvelykoivan.booksearch.core.domain.Result
import com.stanislavvelykoivan.booksearch.core.domain.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.File

class DefaultBookRepository(
    private val remoteBookDataSource: RemoteBookDataSource,
    private val bookDao: BookDao,
    private val searchDao: SearchDao,
    private val fileStorageManager: FileStorage
) : BookRepository {
    override suspend fun searchBooks(
        query: String,
        languages: List<String>?,
        page: Int
    ): Result<List<Book>, DataError.Remote> {
        return remoteBookDataSource
            .searchBooks(query, languages, page)
            .map { dto ->
                dto.results.map {
                    it.toBook()
                }
            }
    }

    override suspend fun getBookById(bookId: Long): Result<Book, DataError.Remote> {
        return remoteBookDataSource
            .getBookById(bookId)
            .map { dto ->
                dto.toBook()
            }
    }


    override fun isBookSaved(bookId: Long): Flow<Boolean> {
        return bookDao.isBookSaved(bookId)
    }


    override suspend fun saveBookToDatabase(book: Book) {
        bookDao.saveEverything(book.toBookEntity(), book.authors.map { it.toAuthorEntity() })
    }

    override fun getSavedBooks(): Flow<List<Book>> {
        return bookDao.getSavedBooks().map { booksWithAuthors ->
            booksWithAuthors.map { it.toBook() }
        }
    }

    override suspend fun getBookFromDatabase(bookId: Long): Book? {
        return bookDao.getBookWithAuthorsById(bookId)?.toBook()
    }

    override suspend fun downloadFormat(
        bookId: Long,
        formatMimeType: String,
        url: String
    ): Result<String, DataError> {
        Log.d("DownloadDebug", "downloadFormat called for ID: $bookId, URL: $url")

        val extension = fileStorageManager.getExtensionFromMime(formatMimeType)
        val targetFile = fileStorageManager.getFinalFile(fileStorageManager.getBaseFile(bookId), extension)

        if (fileStorageManager.isBookDownloaded(bookId, extension)) {
            Log.d("DownloadDebug", "Book $bookId already exists at: ${targetFile.absolutePath}")
            return Result.Success(targetFile.absolutePath)
        }


        val downloadResult = remoteBookDataSource.downloadStreaming(url) { channel ->
            fileStorageManager.saveChannelToFile(channel, targetFile)
        }

        return when (downloadResult) {
            is Result.Success -> {
                Log.d("DownloadDebug", "File saved: ${targetFile.absolutePath}")
                Result.Success(targetFile.absolutePath)
            }
            is Result.Error -> {
                Log.e("DownloadDebug", "Download or Save error: ${downloadResult.error}")
                downloadResult
            }
        }
    }

    override suspend fun getBookFiles(bookId: Long): List<BookFile> {
        return fileStorageManager.getAvailableFilesForBook(bookId)
    }

    override suspend fun openFile(file: File): Result<Unit, DataError.Local> {
        return fileStorageManager.openFile(file)
    }

    override suspend fun deleteBookFromDatabase(bookId: Long) {
        bookDao.deleteBookAndCleanup(bookId)
    }

    override suspend fun deleteBook(bookId: Long): Boolean {
        return fileStorageManager.deleteBook(bookId)
    }

    override fun getLastSearchQuery(): Flow<List<String>> {
        return searchDao.getRecentSearches()
            .map { entities ->
                entities.map { it.toDomain() }
            }
    }

    override suspend fun saveSearchQuery(query: String) {
        if (query.isBlank() || query.length < 2) return

        val entity = SearchHistoryEntity(
            searchQuery = query.trim(),
            lastSearchedAt = System.currentTimeMillis()
        )

        searchDao.upsertSearchQuery(entity)
    }
}