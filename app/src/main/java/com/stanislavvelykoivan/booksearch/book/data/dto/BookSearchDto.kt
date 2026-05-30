package com.stanislavvelykoivan.booksearch.book.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BookSearchDto(
    @SerialName("id") val id: Long,
    @SerialName("title") val title: String,
    @SerialName("authors") val authors: List<PersonDto>,
    @SerialName("translators") val translators: List<PersonDto> = emptyList(),
    @SerialName("subjects") val subjects: List<String> = emptyList(),
    @SerialName("bookshelves") val bookshelves: List<String> = emptyList(),
    @SerialName("languages") val languages: List<String> = emptyList(),
    @SerialName("formats") val formats: Map<String, String>,
    @SerialName("download_count") val downloadCount: Int
)
