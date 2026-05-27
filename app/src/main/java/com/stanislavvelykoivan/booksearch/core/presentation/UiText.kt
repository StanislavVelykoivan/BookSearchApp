package com.stanislavvelykoivan.booksearch.core.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

sealed interface UiText {
    data class DynamicString(val value: String): UiText
    data class StringResourceId(
        @param:androidx.annotation.StringRes val id: Int,
        val args: Array<Any> = emptyArray()
    ): UiText

    @Composable
    fun asString(): String {
        return when(this) {
            is DynamicString -> value
            is StringResourceId -> stringResource(id = id, formatArgs = args)
        }
    }
}