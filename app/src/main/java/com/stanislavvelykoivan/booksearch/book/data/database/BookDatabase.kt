package com.stanislavvelykoivan.booksearch.book.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.stanislavvelykoivan.booksearch.book.data.database.dao.BookDao
import com.stanislavvelykoivan.booksearch.book.data.database.dao.SearchDao
import com.stanislavvelykoivan.booksearch.book.data.database.entitys.AuthorEntity
import com.stanislavvelykoivan.booksearch.book.data.database.entitys.BookAuthorCrossRef
import com.stanislavvelykoivan.booksearch.book.data.database.entitys.BookEntity
import com.stanislavvelykoivan.booksearch.book.data.database.entitys.SearchHistoryEntity

@Database(
    entities = [BookEntity::class, AuthorEntity::class, BookAuthorCrossRef::class, SearchHistoryEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(BookTypeConverter::class)
abstract class BookDatabase : RoomDatabase() {

    abstract fun bookDao(): BookDao
    abstract fun searchDao(): SearchDao


    companion object {
        const val DATABASE_NAME = "book.db"
    }
}