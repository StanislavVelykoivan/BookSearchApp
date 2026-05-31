package com.stanislavvelykoivan.booksearch.book.data.repository

import android.util.Log
import com.stanislavvelykoivan.booksearch.book.data.database.dao.BookDao
import com.stanislavvelykoivan.booksearch.book.data.files.FileStorage
import com.stanislavvelykoivan.booksearch.book.data.mappers.toAuthorEntity
import com.stanislavvelykoivan.booksearch.book.data.mappers.toBook
import com.stanislavvelykoivan.booksearch.book.data.mappers.toBookEntity
import com.stanislavvelykoivan.booksearch.book.data.network.RemoteBookDataSource
import com.stanislavvelykoivan.booksearch.book.domain.Book
import com.stanislavvelykoivan.booksearch.book.domain.BookRepository
import com.stanislavvelykoivan.booksearch.core.domain.DataError
import com.stanislavvelykoivan.booksearch.core.domain.Result
import com.stanislavvelykoivan.booksearch.core.domain.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DefaultBookRepository(
    private val remoteBookDataSource: RemoteBookDataSource,
    private val bookDao: BookDao,
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

        if (fileStorageManager.isBookDownloaded(bookId, extension)) {
            val file = fileStorageManager.getFinalFile(fileStorageManager.getBaseFile(bookId), extension)
            Log.d("DownloadDebug", "Book $bookId already exists at: ${file.absolutePath}")
            return Result.Success(file.absolutePath)
        }

        val targetFile = fileStorageManager.getFinalFile(fileStorageManager.getBaseFile(bookId), extension)

        val channelResult = remoteBookDataSource.downloadBookChannel(url)

        if (channelResult is Result.Error) {
            Log.e("DownloadDebug", "Network error: ${channelResult.error}")
            return channelResult
        }

        val channel = (channelResult as Result.Success).data
        val saveResult = fileStorageManager.saveChannelToFile(channel, targetFile)

        return when (saveResult) {
            is Result.Success -> {
                Log.d("DownloadDebug", "File saved: ${targetFile.absolutePath}")
                Result.Success(targetFile.absolutePath)
            }
            is Result.Error -> {
                Log.e("DownloadDebug", "File save error: ${saveResult.error}")
                Result.Error(saveResult.error)
            }
        }
    }
}