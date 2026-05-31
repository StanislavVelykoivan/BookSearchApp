package com.stanislavvelykoivan.booksearch.book.data.database

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json

class BookTypeConverter {


    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        return Json.decodeFromString(value)
    }

    @TypeConverter
    fun fromMap(value: Map<String, String>): String = Json.encodeToString(value)

    @TypeConverter
    fun toMap(value: String): Map<String, String> = Json.decodeFromString(value)
}