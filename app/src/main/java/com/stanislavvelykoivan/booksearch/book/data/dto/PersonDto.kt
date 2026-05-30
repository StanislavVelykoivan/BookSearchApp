package com.stanislavvelykoivan.booksearch.book.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PersonDto (
    @SerialName("name") val name: String,
    @SerialName("birth_year") val birthYear: Int?,
    @SerialName("death_year") val deathYear: Int?
)