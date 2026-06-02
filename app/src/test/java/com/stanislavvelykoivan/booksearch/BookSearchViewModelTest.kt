package com.stanislavvelykoivan.booksearch

import app.cash.turbine.test
import com.google.common.truth.Truth.assertThat
import com.stanislavvelykoivan.booksearch.book.domain.Book
import com.stanislavvelykoivan.booksearch.book.domain.BookRepository
import com.stanislavvelykoivan.booksearch.book.presentation.book_search.BookSearchAction
import com.stanislavvelykoivan.booksearch.book.presentation.book_search.BookSearchViewModel
import com.stanislavvelykoivan.booksearch.core.domain.DataError
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import com.stanislavvelykoivan.booksearch.core.domain.Result

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class BookSearchViewModelTest {

    private lateinit var viewModel: BookSearchViewModel
    private val bookRepository = mockk<BookRepository>(relaxed = true)

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        every { bookRepository.getSavedBooks() } returns flowOf(emptyList())
        every { bookRepository.getLastSearchQuery() } returns flowOf(emptyList())

        viewModel = BookSearchViewModel(bookRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `OnQueryChange updates state with new query`() = runTest {
        val newQuery = "Kotlin"

        viewModel.onAction(BookSearchAction.OnQueryChange(newQuery))

        assertThat(viewModel.state.value.query).isEqualTo(newQuery)
    }

    @Test
    fun `OnSearchClick triggers search and updates state with results`() = runTest {
        val query = "Android"
        val mockBooks = listOf(
            Book(id = 1L, title = "Android Guide",
                authors = emptyList(),
                languages = listOf("en"),
                subjects = emptyList(),
                bookshelves = emptyList(),
                downloadCount = 0,
                coverUrl = null,
                formats = emptyMap()
            )
        )


        coEvery { bookRepository.searchBooks(any(), any(), any()) } coAnswers {
            kotlinx.coroutines.delay(1)
            Result.Success(mockBooks)
        }
        coEvery { bookRepository.saveSearchQuery(any()) } returns Unit

        viewModel.state.test {
            awaitItem()

            viewModel.onAction(BookSearchAction.OnQueryChange(query))
            awaitItem()

            viewModel.onAction(BookSearchAction.OnSearchClick)


            assertThat(awaitItem().isLoading).isTrue()


            val resultState = awaitItem()
            assertThat(resultState.isLoading).isFalse()
            assertThat(resultState.searchResult).isEqualTo(mockBooks)
        }
    }

    @Test
    fun `search with short query does nothing`() = runTest {
        val shortQuery = "k"

        viewModel.onAction(BookSearchAction.OnQueryChange(shortQuery))
        viewModel.onAction(BookSearchAction.OnSearchClick)

        coVerify(exactly = 0) { bookRepository.searchBooks(any(), any(), any()) }
    }

    @Test
    fun `search error updates state with error message`() = runTest {
        coEvery { bookRepository.searchBooks(any(), any(), any()) } returns Result.Error(DataError.Remote.NO_INTERNET)

        viewModel.onAction(BookSearchAction.OnQueryChange("Testing Errors"))
        viewModel.onAction(BookSearchAction.OnSearchClick)

        viewModel.state.test {
            val finalState = expectMostRecentItem()
            assertThat(finalState.error).isNotNull()
            assertThat(finalState.isLoading).isFalse()
        }
    }

    @Test
    fun `OnLanguageFilterClick updates selected languages and resets page`() = runTest {
        val languages = listOf("uk", "en")


        viewModel.onAction(BookSearchAction.OnLoadNextPage)
        viewModel.onAction(BookSearchAction.OnLoadNextPage)

        viewModel.onAction(BookSearchAction.OnLanguageFilterClick(languages))

        val currentState = viewModel.state.value
        assertThat(currentState.selectedLanguages).isEqualTo(languages)
        assertThat(currentState.page).isEqualTo(1)
    }

    @Test
    fun `OnTabSelected updates onTabSelected index`() = runTest {
        val testTabIndex = 1

        viewModel.onAction(BookSearchAction.OnTabSelected(testTabIndex))

        assertThat(viewModel.state.value.onTabSelected).isEqualTo(testTabIndex)
    }
}