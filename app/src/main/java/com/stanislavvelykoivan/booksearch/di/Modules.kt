package com.stanislavvelykoivan.booksearch.di

import androidx.room.Room
import com.stanislavvelykoivan.booksearch.book.data.database.BookDatabase
import com.stanislavvelykoivan.booksearch.book.data.files.FileStorage
import com.stanislavvelykoivan.booksearch.book.data.files.FileStorageManager
import com.stanislavvelykoivan.booksearch.book.data.network.KtorRemoteBookDataSource
import com.stanislavvelykoivan.booksearch.book.data.network.RemoteBookDataSource
import com.stanislavvelykoivan.booksearch.book.data.repository.DefaultBookRepository
import com.stanislavvelykoivan.booksearch.book.domain.BookRepository
import com.stanislavvelykoivan.booksearch.book.domain.DownloadBookUseCase
import com.stanislavvelykoivan.booksearch.book.presentation.book_detail.BookDetailViewModel
import com.stanislavvelykoivan.booksearch.book.presentation.book_search.BookSearchViewModel
import com.stanislavvelykoivan.booksearch.core.data.HttpClientFactory
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind

import org.koin.dsl.module

val appModule = module {
    single<HttpClientEngine> { OkHttp.create() }

    single {
        HttpClientFactory.create(get())
    }

    single {
        Room.databaseBuilder(
            androidContext(),
            BookDatabase::class.java,
            BookDatabase.DATABASE_NAME
        ).build()
    }


    single { get<BookDatabase>().bookDao() }
    single { get<BookDatabase>().authorDao() }


    singleOf(::FileStorageManager).bind<FileStorage>()
    singleOf(::KtorRemoteBookDataSource).bind<RemoteBookDataSource>()
    singleOf(::DefaultBookRepository).bind<BookRepository>()
    singleOf(::DownloadBookUseCase)

    viewModelOf(::BookSearchViewModel)
    viewModelOf(::BookDetailViewModel)
}