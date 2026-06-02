package com.stanislavvelykoivan.booksearch

import android.content.ActivityNotFoundException
import android.content.Context
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import com.stanislavvelykoivan.booksearch.book.data.files.FileStorageManager
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.junit.Before
import org.junit.Test
import java.io.File
import com.google.common.truth.Truth.assertThat
import com.stanislavvelykoivan.booksearch.core.domain.DataError
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.test.runTest
import com.stanislavvelykoivan.booksearch.core.domain.Result
import io.mockk.mockkConstructor
import io.mockk.unmockkConstructor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class FileStorageManagerTest {

    private lateinit var fileStorageManager: FileStorageManager
    private val context = mockk<Context>(relaxed = true)
    private val filesDir = File("/tmp/test_android_files")
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        every { context.filesDir } returns filesDir
        fileStorageManager = FileStorageManager(context)

        mockkStatic(MimeTypeMap::class)
        mockkStatic(android.util.Log::class)

        every { android.util.Log.d(any(), any()) } returns 0
        every { android.util.Log.e(any(), any()) } returns 0
        every { android.util.Log.e(any(), any(), any()) } returns 0
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getBookDir returns correct path with bookId`() {
        val bookId = 123L
        val expected = File(filesDir, "books/$bookId")

        val actual = fileStorageManager.getBookDir(bookId)

        assertThat(actual.absolutePath).isEqualTo(expected.absolutePath)
    }

    @Test
    fun `getExtensionFromMime returns correct extension for epub`() {
        val mimeType = "application/epub+zip"
        val mockMimeMap = mockk<MimeTypeMap>()

        every { MimeTypeMap.getSingleton() } returns mockMimeMap
        every { mockMimeMap.getExtensionFromMimeType("application/epub+zip") } returns "epub"

        val extension = fileStorageManager.getExtensionFromMime(mimeType)

        assertThat(extension).isEqualTo("epub")
    }

    @Test
    fun `getExtensionFromMime returns bin for unknown mime`() {
        val mimeType = "unknown/type"
        val mockMimeMap = mockk<MimeTypeMap>()

        every { MimeTypeMap.getSingleton() } returns mockMimeMap
        every { mockMimeMap.getExtensionFromMimeType(any()) } returns null

        val extension = fileStorageManager.getExtensionFromMime(mimeType)

        assertThat(extension).isEqualTo("bin")
    }

    @Test
    fun `getFinalFile combines base file and extension correctly`() {
        val baseFile = File("/tmp/book_123")
        val extension = "pdf"

        val finalFile = fileStorageManager.getFinalFile(baseFile, extension)

        assertThat(finalFile.name).isEqualTo("book_123.pdf")
        assertThat(finalFile.extension).isEqualTo("pdf")
    }

    @Test
    fun `isBookDownloaded returns false if file does not exist`() = runTest {
        val bookId = 999L
        val extension = "txt"

        val isDownloaded = fileStorageManager.isBookDownloaded(bookId, extension)

        assertThat(isDownloaded).isFalse()
    }

    @Test
    fun `getBaseFile returns correct file naming convention`() {
        val bookId = 555L
        val baseFile = fileStorageManager.getBaseFile(bookId)

        assertThat(baseFile.name).isEqualTo("book_555")
        assertThat(baseFile.parentFile?.name).isEqualTo("555")
    }
    @Test
    fun `deleteBook returns true if directory does not exist`() = runTest {
        val nonExistentBookId = 999999L

        val result = fileStorageManager.deleteBook(nonExistentBookId)

        assertThat(result).isTrue()
    }

    @Test
    fun `getAvailableFilesForBook returns empty list if directory is missing`() = runTest {
        val result = fileStorageManager.getAvailableFilesForBook(888L)

        assertThat(result).isEmpty()
    }

    @Test
    fun `getExistingFile returns null if no files in directory`() = runTest {
        val bookId = 111L
        val dir = fileStorageManager.getBookDir(bookId)
        dir.mkdirs()

        val file = fileStorageManager.getExistingFile(bookId)

        assertThat(file).isNull()

        dir.deleteRecursively()
    }

    @Test
    fun `getExistingFile returns file when it exists`() = runTest {
        val bookId = 222L
        val dir = fileStorageManager.getBookDir(bookId)
        dir.mkdirs()
        val testFile = File(dir, "book_222.epub")
        testFile.createNewFile()

        val foundFile = fileStorageManager.getExistingFile(bookId)

        assertThat(foundFile).isNotNull()
        assertThat(foundFile?.name).isEqualTo("book_222.epub")

        dir.deleteRecursively()
    }

    @Test
    fun `getExtensionFromMime handles mime types with parameters`() {
        val mimeType = "application/pdf; charset=UTF-8"
        val mockMimeMap = mockk<MimeTypeMap>()

        every { MimeTypeMap.getSingleton() } returns mockMimeMap
        every { mockMimeMap.getExtensionFromMimeType("application/pdf") } returns "pdf"

        val extension = fileStorageManager.getExtensionFromMime(mimeType)

        assertThat(extension).isEqualTo("pdf")
    }

    @Test
    fun `saveChannelToFile successfully writes data and returns Success`() = runTest {
        val bookId = 777L
        val testFile = File(filesDir, "books/$bookId/test_save.bin")

        val testData = "Hello, Kotlin!".toByteArray()
        val channel = ByteReadChannel(testData)

        val result = fileStorageManager.saveChannelToFile(channel, testFile)

        assertThat(result is Result.Success).isTrue()
        assertThat(testFile.exists()).isTrue()
        assertThat(testFile.readBytes()).isEqualTo(testData)

        testFile.delete()
    }

    @Test
    fun `saveChannelToFile returns Error and deletes file on exception`() = runTest {
        val readOnlyFile = File("/proc/test_file")
        val channel = ByteReadChannel("data".toByteArray())

        val result = fileStorageManager.saveChannelToFile(channel, readOnlyFile)

        assertThat(result is Result.Error).isTrue()
        assertThat((result as Result.Error).error).isEqualTo(DataError.Local.UNKNOWN)
    }

    @Test
    fun `openFile successfully starts activity and returns Success`() = runTest {
        val testFile = File(filesDir, "test_book.pdf")

        every { context.packageName } returns "com.stanislavvelykoivan.booksearch"
        every { context.contentResolver.getType(any()) } returns "application/pdf"

        mockkStatic(FileProvider::class)
        mockkStatic(android.net.Uri::class)

        val mockUri = mockk<android.net.Uri>(relaxed = true)
        every { FileProvider.getUriForFile(any(), any(), any()) } returns mockUri

        mockkConstructor(android.content.Intent::class)
        every { anyConstructed<android.content.Intent>().setDataAndType(any(), any()) } returns mockk(relaxed = true)
        every { anyConstructed<android.content.Intent>().addFlags(any()) } returns mockk(relaxed = true)

        val result = fileStorageManager.openFile(testFile)

        assertThat(result is Result.Success).isTrue()

        unmockkConstructor(android.content.Intent::class)
    }

    @Test
    fun `openFile returns NO_APP_TO_OPEN_FILE when no app installed`() = runTest {
        val testFile = File(filesDir, "test_book.pdf")

        every { context.packageName } returns "com.stanislavvelykoivan.booksearch"
        every { context.contentResolver.getType(any()) } returns "application/pdf"

        mockkStatic(FileProvider::class)
        val mockUri = mockk<android.net.Uri>(relaxed = true)
        every { FileProvider.getUriForFile(any(), any(), any()) } returns mockUri

        mockkConstructor(android.content.Intent::class)
        every { anyConstructed<android.content.Intent>().setDataAndType(any(), any()) } returns mockk(relaxed = true)
        every { anyConstructed<android.content.Intent>().addFlags(any()) } returns mockk(relaxed = true)

        every { context.startActivity(any()) } throws ActivityNotFoundException()

        val result = fileStorageManager.openFile(testFile)

        assertThat(result is Result.Error).isTrue()
        assertThat((result as Result.Error).error).isEqualTo(DataError.Local.NO_APP_TO_OPEN_FILE)

        unmockkConstructor(android.content.Intent::class)
    }
}
