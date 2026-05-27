package com.stanislavvelykoivan.booksearch.di

import com.stanislavvelykoivan.booksearch.core.data.HttpClientFactory
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.dsl.module

val appModule = module {
    single<HttpClientEngine> { OkHttp.create() }

    single {
        HttpClientFactory.create(get())
    }
}