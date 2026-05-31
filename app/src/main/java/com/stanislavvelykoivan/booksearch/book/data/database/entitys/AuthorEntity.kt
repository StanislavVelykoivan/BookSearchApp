package com.stanislavvelykoivan.booksearch.book.data.database.entitys

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "authors",
    indices = [Index(value = ["name"], unique = true)])
data class AuthorEntity(
    @PrimaryKey(autoGenerate = true) val authorId: Long,
    val name: String,
    val birthYear: Int?,
    val deathYear: Int?
)