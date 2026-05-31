package com.stanislavvelykoivan.booksearch.book.data.files

import com.stanislavvelykoivan.booksearch.core.domain.DataError
import io.ktor.utils.io.ByteReadChannel
import java.io.File
import com.stanislavvelykoivan.booksearch.core.domain.Result

interface FileStorage {
    fun getBookDir(bookId: Long): File
    fun getBaseFile(bookId: Long): File
    fun getExtensionFromMime(mimeType: String?): String

    fun deleteBook(bookId: Long): Boolean
    fun getFinalFile(baseFile: File, extension: String): File
    fun isBookDownloaded(bookId: Long, extension: String): Boolean
    suspend fun saveChannelToFile(channel: ByteReadChannel, file: File): Result<Unit, DataError.Local>
    fun getExistingFile(bookId: Long): File?
}