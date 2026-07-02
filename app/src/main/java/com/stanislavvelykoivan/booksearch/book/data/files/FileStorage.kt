package com.stanislavvelykoivan.booksearch.book.data.files

import com.stanislavvelykoivan.booksearch.book.domain.BookFile
import com.stanislavvelykoivan.booksearch.core.domain.DataError
import com.stanislavvelykoivan.booksearch.core.domain.Result
import io.ktor.utils.io.ByteReadChannel
import java.io.File

interface FileStorage {
    fun getBookDir(bookId: Long): File
    fun getBaseFile(bookId: Long): File
    fun getExtensionFromMime(mimeType: String?): String
    suspend fun deleteBook(bookId: Long): Boolean
    fun getFinalFile(baseFile: File, extension: String): File
    suspend fun isBookDownloaded(bookId: Long, extension: String): Boolean
    suspend fun getExistingFile(bookId: Long): File?
    suspend fun getAvailableFilesForBook(bookId: Long): List<BookFile>

    suspend fun openFile(file: File): Result<Unit, DataError.Local>
    suspend fun saveChannelToFile(
        channel: ByteReadChannel,
        file: File,
        contentLength: Long?,
        onProgress: (Float) -> Unit
    ): Result<Unit, DataError.Local>
}