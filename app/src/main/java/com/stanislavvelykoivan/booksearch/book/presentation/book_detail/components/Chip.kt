package com.stanislavvelykoivan.booksearch.book.presentation.book_detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.stanislavvelykoivan.booksearch.core.presentation.OnPrimary
import com.stanislavvelykoivan.booksearch.core.presentation.Secondary

@Composable
fun Chip(
    modifier: Modifier = Modifier,
    chipContent: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(OnPrimary)
            .padding(
                vertical = 8.dp,
                horizontal = 16.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        chipContent()
    }
}
