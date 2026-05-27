package com.stanislavvelykoivan.booksearch.app

import android.app.Application
import com.stanislavvelykoivan.booksearch.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class BookSearchApp: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@BookSearchApp)
            modules(appModule)
        }
    }
}
