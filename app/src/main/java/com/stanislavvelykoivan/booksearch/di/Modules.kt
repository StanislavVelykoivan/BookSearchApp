package com.stanislavvelykoivan.booksearch.di

import com.stanislavvelykoivan.booksearch.book.data.network.KtorRemoteBookDataSource
import com.stanislavvelykoivan.booksearch.book.data.network.RemoteBookDataSource
import com.stanislavvelykoivan.booksearch.book.data.repository.DefaultBookRepository
import com.stanislavvelykoivan.booksearch.book.domain.BookRepository
import com.stanislavvelykoivan.booksearch.book.presentation.book_detail.BookDetailViewModel
import com.stanislavvelykoivan.booksearch.book.presentation.book_search.BookSearchViewModel
import com.stanislavvelykoivan.booksearch.core.data.HttpClientFactory
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind

import org.koin.dsl.module

val appModule = module {
    single<HttpClientEngine> { OkHttp.create() }

    single {
        HttpClientFactory.create(get())
    }

    singleOf(::KtorRemoteBookDataSource).bind<RemoteBookDataSource>()
    singleOf(::DefaultBookRepository).bind<BookRepository>()

    viewModelOf(::BookSearchViewModel)
    viewModelOf(::BookDetailViewModel)
}