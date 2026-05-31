package com.stanislavvelykoivan.booksearch.book.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.stanislavvelykoivan.booksearch.book.data.database.entitys.AuthorEntity

@Dao
interface AuthorDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAuthor(author: AuthorEntity): Long

    @Query("SELECT authorId FROM authors WHERE name = :name")
    suspend fun getAuthorIdByName(name: String): Long?
}