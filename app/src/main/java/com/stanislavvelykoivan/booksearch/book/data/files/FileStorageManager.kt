package com.stanislavvelykoivan.booksearch.book.data.files

import android.content.Context
import android.util.Log
import android.webkit.MimeTypeMap
import com.stanislavvelykoivan.booksearch.core.domain.DataError
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readRemaining
import kotlinx.io.readByteArray
import java.io.File
import com.stanislavvelykoivan.booksearch.core.domain.Result
class FileStorageManager(private val context: Context):FileStorage {

    override fun getBookDir(bookId: Long) = File(context.filesDir, "books/$bookId")

    override fun getBaseFile(bookId: Long) = File(getBookDir(bookId), "book_$bookId")

    override fun getExtensionFromMime(mimeType: String?): String {
        val type = mimeType?.split(";")?.first()?.trim()
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(type) ?: "bin"
    }

    override fun getExistingFile(bookId: Long): File? =
        getBookDir(bookId).listFiles()?.find { it.name.startsWith("book_$bookId.") }

    override fun deleteBook(bookId: Long) = getBookDir(bookId).deleteRecursively()

    override fun getFinalFile(baseFile: File, extension: String) =
        File("${baseFile.absolutePath}.$extension")

    override fun isBookDownloaded(bookId: Long, extension: String): Boolean {
        val file = File(getBookDir(bookId), "book_$bookId.$extension")
        return file.exists()
    }

    override suspend fun saveChannelToFile(channel: ByteReadChannel, file: File): Result<Unit, DataError.Local> {
        Log.d("FileStorage", "Starting to save data to: ${file.absolutePath}")
        return try {
            file.parentFile?.exists()?.let {
                if (!it) {
                    val created = file.parentFile?.mkdirs()
                    Log.d("FileStorage", "Directory created: $created")
                }
            }

            file.outputStream().buffered().use { output ->
                var totalBytes = 0L
                while (!channel.isClosedForRead) {
                    val packet = channel.readRemaining(8192)
                    if (!packet.exhausted()) {
                        val bytes = packet.readByteArray()
                        output.write(bytes)
                        totalBytes += bytes.size
                    }
                }
                Log.d("FileStorage", "File save completed. Total bytes written: $totalBytes")
            }

            Result.Success(Unit)
        } catch (e: Exception) {
            Log.e("FileStorage", "Error occurred while saving file: ${e.message}", e)
            if (file.exists()) file.delete()
            Result.Error(DataError.Local.DISK_FULL)
        }
    }
}