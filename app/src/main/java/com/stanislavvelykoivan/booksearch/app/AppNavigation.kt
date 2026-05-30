package com.stanislavvelykoivan.booksearch.app

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.stanislavvelykoivan.booksearch.book.presentation.book_detail.BookDetailScreenRoot
import com.stanislavvelykoivan.booksearch.book.presentation.book_search.BookSearchScreenRoot

@Composable
fun AppNavigation(navController: NavHostController, modifier: Modifier) {
    NavHost(navController = navController, startDestination = Route.SearchRoute, modifier = modifier) {
        composable<Route.SearchRoute> {
            BookSearchScreenRoot(
                onBookClick = { id ->
                    navController.navigate(Route.DetailRoute(bookId = id))
                }
            )
        }
        composable<Route.DetailRoute> { backStackEntry ->
            val route = backStackEntry.toRoute<Route.DetailRoute>()
            BookDetailScreenRoot(bookId = route.bookId)
        }
    }
}