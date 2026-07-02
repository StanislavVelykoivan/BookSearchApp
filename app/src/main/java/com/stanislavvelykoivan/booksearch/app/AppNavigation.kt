package com.stanislavvelykoivan.booksearch.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.stanislavvelykoivan.booksearch.book.presentation.book_detail.BookDetailScreenRoot
import com.stanislavvelykoivan.booksearch.book.presentation.book_search.BookSearchScreenRoot
import com.stanislavvelykoivan.booksearch.core.presentation.OnBackground
import com.stanislavvelykoivan.booksearch.core.presentation.Surface

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Route.SearchRoute,
        modifier = modifier
    ) {
        composable<Route.SearchRoute> {
            var selectedBook by remember { mutableStateOf<Long?>(null) }
            BookSearchScreenRoot(
                onBookClick = {id ->
                    selectedBook = id
                }
            )

            selectedBook?.let { bookId ->
                Dialog(
                    onDismissRequest = {
                        selectedBook = null
                    }
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(OnBackground)
                    ){
                        BookDetailScreenRoot (
                            bookId = bookId,
                            onBackClick = {
                                selectedBook = null
                            }
                        )
                    }
                }
            }
        }
    }
}
//composable<Route.SearchRoute> {
//            BookSearchScreenRoot(
//                onBookClick = { id ->
//                    navController.navigate(Route.DetailRoute(bookId = id))
//                }
//            )
//        }
//
//        composable<Route.DetailRoute> { backStackEntry ->
//            val route = backStackEntry.toRoute<Route.DetailRoute>()
//
//            BookDetailScreenRoot(
//                bookId = route.bookId,
//                onBackClick = {
//                    navController.popBackStack()
//                }
//            )
//        }