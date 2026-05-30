package com.stanislavvelykoivan.booksearch.app

import kotlinx.serialization.Serializable

sealed interface Route {

    @Serializable
    object SearchRoute

    @Serializable
    data class DetailRoute(val bookId: Long)
}

