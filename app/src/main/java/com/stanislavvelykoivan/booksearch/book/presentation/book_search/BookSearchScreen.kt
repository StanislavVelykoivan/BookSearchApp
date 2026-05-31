package com.stanislavvelykoivan.booksearch.book.presentation.book_search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stanislavvelykoivan.booksearch.R
import com.stanislavvelykoivan.booksearch.book.presentation.book_search.components.BookLanguagesFilter
import com.stanislavvelykoivan.booksearch.book.presentation.book_search.components.BookList
import com.stanislavvelykoivan.booksearch.book.presentation.book_search.components.BookSearchBar
import com.stanislavvelykoivan.booksearch.core.presentation.OnPrimary

import com.stanislavvelykoivan.booksearch.core.presentation.Primary
import com.stanislavvelykoivan.booksearch.core.presentation.Secondary
import org.koin.androidx.compose.koinViewModel

@Composable
fun BookSearchScreenRoot(
    viewModel: BookSearchViewModel = koinViewModel(),
    onBookClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    BookSearchScreen(
        state = state,
        onAction = { action ->
            if (action is BookSearchAction.OnBookClick) {
                onBookClick(action.bookId)
            } else {
                viewModel.onAction(action)
            }
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookSearchScreen(
    state: BookSearchState,
    onAction: (BookSearchAction) -> Unit,
    modifier: Modifier = Modifier
) {

    val pagerState = rememberPagerState { 2 }
    val searchResultListState = rememberLazyListState()
    val savedBooksListState = rememberLazyListState()
    val sheetState = rememberModalBottomSheetState()
    var showFilterSheet by remember { mutableStateOf(false) }

    LaunchedEffect(state.searchResult) {
        searchResultListState.animateScrollToItem(0)
    }
    LaunchedEffect(state.onTabSelected) {
        pagerState.animateScrollToPage(state.onTabSelected)
    }
    LaunchedEffect(pagerState.currentPage) {
        onAction(BookSearchAction.OnTabSelected(pagerState.currentPage))
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = Primary)
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            BookSearchBar(
                query = state.query,
                onQueryChange = { onAction(BookSearchAction.OnQueryChange(it)) },
                onSearch = { onAction(BookSearchAction.OnSearchClick) },
                modifier = Modifier.weight(1f)
            )


            IconButton(onClick = { showFilterSheet = true }) {
                Icon(Icons.Default.FilterList, contentDescription = null)
            }


        }

        Spacer(
            modifier = Modifier
                .height(32.dp)
                .fillMaxWidth()
        )

        Surface(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            color = OnPrimary
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TabRow(
                    selectedTabIndex = state.onTabSelected,
                    modifier = Modifier
                        .fillMaxWidth(),
                    containerColor = OnPrimary,
                    contentColor = Primary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            color = Primary,
                            modifier = Modifier
                                .tabIndicatorOffset(tabPositions[state.onTabSelected])
                        )
                    }
                ) {
                    Tab(
                        selected = state.onTabSelected == 0,
                        onClick = { onAction(BookSearchAction.OnTabSelected(0)) },
                        selectedContentColor = Primary,
                        unselectedContentColor = Secondary
                    ) {
                        Text(
                            text = stringResource(R.string.search_tab_title),
                            modifier = Modifier
                                .padding(vertical = 12.dp)
                        )
                    }
                    Tab(
                        selected = state.onTabSelected == 1,
                        onClick = { onAction(BookSearchAction.OnTabSelected(1)) },
                        selectedContentColor = Primary,
                        unselectedContentColor = Secondary
                    ) {
                        Text(
                            text = stringResource(R.string.saved_tab_title),
                            modifier = Modifier
                                .padding(vertical = 12.dp)
                        )
                    }
                }
                Spacer(
                    modifier = Modifier
                        .height(4.dp)
                )

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) { page ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        when (page) {
                            0 -> {
                                if (state.isLoading) {
                                    CircularProgressIndicator()
                                } else {
                                    when {
                                        state.error != null -> {
                                            Text(
                                                text = state.error.asString(),
                                                textAlign = TextAlign.Center,
                                                style = MaterialTheme.typography.headlineMedium,
                                                color = MaterialTheme.colorScheme.error
                                            )
                                        }

                                        state.searchResult.isEmpty() -> {
                                            Text(
                                                text = stringResource(R.string.no_search_results),
                                                textAlign = TextAlign.Center,
                                                style = MaterialTheme.typography.headlineMedium
                                            )
                                        }

                                        else -> {
                                            BookList(
                                                books = state.searchResult,
                                                onBookClick = {
                                                    onAction(
                                                        BookSearchAction.OnBookClick(
                                                            it.id
                                                        )
                                                    )
                                                },
                                                scrollState = searchResultListState
                                            )
                                        }

                                    }
                                }

                            }

                            1 -> {
                                if (state.savedBooks.isEmpty()) {
                                    Text(
                                        text = stringResource(R.string.no_saved_books),
                                        textAlign = TextAlign.Center,
                                        style = MaterialTheme.typography.headlineMedium
                                    )
                                } else {
                                    BookList(
                                        books = state.savedBooks,
                                        onBookClick = { onAction(BookSearchAction.OnBookClick(it.id)) },
                                        scrollState = savedBooksListState
                                    )
                                }
                            }
                        }
                    }
                }
            }

        }
        if (showFilterSheet) {
            ModalBottomSheet(
                onDismissRequest = { showFilterSheet = false },
                sheetState = sheetState
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 32.dp)
                ) {
                    Text(
                        text = stringResource(R.string.filter_title),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    BookLanguagesFilter(
                        selectedLanguages = state.selectedLanguages,
                        onLanguageSelected = { newLanguages ->
                            onAction(BookSearchAction.OnLanguageFilterClick(newLanguages))
                        }
                    )
                }
            }
        }
    }

}