package com.stanislavvelykoivan.booksearch.book.data.files

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.text.format.Formatter
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import com.stanislavvelykoivan.booksearch.book.domain.BookFile
import com.stanislavvelykoivan.booksearch.core.domain.DataError
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readRemaining
import kotlinx.io.readByteArray
import java.io.File
import com.stanislavvelykoivan.booksearch.core.domain.Result
import io.ktor.utils.io.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FileStorageManager(private val context: Context):FileStorage {

    override fun getBookDir(bookId: Long) = File(context.filesDir, "books/$bookId")

    override fun getBaseFile(bookId: Long) = File(getBookDir(bookId), "book_$bookId")

    override fun getExtensionFromMime(mimeType: String?): String {
        val type = mimeType?.split(";")?.first()?.trim()
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(type) ?: "bin"
    }

    override suspend fun getExistingFile(bookId: Long): File? =
        getBookDir(bookId).listFiles()?.find { it.name.startsWith("book_$bookId.") }

    override suspend fun deleteBook(bookId: Long): Boolean {
        val dir = getBookDir(bookId)
        if (!dir.exists()) return true

        val success = dir.deleteRecursively()
        if (!success) {
            Log.e("FileStorage", "Failed to delete book directory: ${dir.absolutePath}")
        }
        return success
    }

    override fun getFinalFile(baseFile: File, extension: String) =
        File("${baseFile.absolutePath}.$extension")

    override suspend fun isBookDownloaded(bookId: Long, extension: String): Boolean {
        val file = File(getBookDir(bookId), "book_$bookId.$extension")
        return file.exists()
    }

    override suspend fun saveChannelToFile(
        channel: ByteReadChannel,
        file: File,
        contentLength: Long?,
        onProgress: (Float) -> Unit
    ): Result<Unit, DataError.Local> = withContext(Dispatchers.IO) {

        Log.d("FileStorage", "Starting to save data to: ${file.absolutePath}")

        try {
            file.parentFile?.let { parent ->
                if (!parent.exists()) {
                    val created = parent.mkdirs()
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

                        if (contentLength != null && contentLength > 0){
                            val progress = totalBytes.toFloat() / contentLength.toFloat()
                            onProgress(progress.coerceIn(0f,1f))
                        }
                    }
                }

                Log.d("FileStorage", "File save completed. Total bytes written: $totalBytes")
            }

            onProgress(1f)
            Result.Success(Unit)

        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e("FileStorage", "Error occurred while saving file: ${e.message}", e)

            if (file.exists()) file.delete()

            Result.Error(DataError.Local.UNKNOWN)
        }
    }

    override suspend fun openFile(file: File): Result<Unit, DataError.Local> =
        withContext(Dispatchers.Main) {
            try {
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.provider",
                    file
                )
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(uri, context.contentResolver.getType(uri) ?: "*/*")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
                Result.Success(Unit)
            } catch (e: ActivityNotFoundException) {
                Log.e("FileStorage", "No app found to open file: ${file.name}", e)
                Result.Error(DataError.Local.NO_APP_TO_OPEN_FILE)
            } catch (_: Exception) {
                Result.Error(DataError.Local.UNKNOWN)
            }
        }

    override suspend fun getAvailableFilesForBook(bookId: Long): List<BookFile> {
        val bookDir = File(context.filesDir, "books/$bookId")
        if (!bookDir.exists()) return emptyList()

        return bookDir.listFiles()?.map { file ->
            BookFile(
                name = file.name,
                format = file.extension,
                size = Formatter.formatFileSize(context, file.length()),
                file = file
            )
        } ?: emptyList()
    }
}