package com.stanislavvelykoivan.booksearch.book.presentation.book_search.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.stanislavvelykoivan.booksearch.book.domain.Book
import com.stanislavvelykoivan.booksearch.core.presentation.OnPrimary
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.stanislavvelykoivan.booksearch.R
import coil3.compose.AsyncImage
import com.stanislavvelykoivan.booksearch.book.domain.Author


@Composable
fun BookItem(
    book: Book,
    onBookClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(8.dp)
            .clickable(onClick = onBookClick),
        colors = CardDefaults.cardColors(containerColor = OnPrimary),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            AsyncImage(
                model = book.coverUrl,
                contentDescription = book.title,
                modifier = Modifier
                    .size(96.dp),
                contentScale = ContentScale.Fit,
                error = painterResource(id = R.drawable.book_foreground),
                placeholder = painterResource(id = R.drawable.book_foreground)
            )


            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2
                )
                Text(
                    text = book.authors.joinToString(", ") { author ->
                        author.name
                    },
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = book.languages.joinToString(", "),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BookItemPreview() {
    val fakeBook = Book(
        id = 1,
        title = "War and Peace",
        authors = listOf(Author(name = "Leo Tolstoy", null, null)),
        languages = listOf("en"),
        subjects = listOf("History", "Philosophy"),
        bookshelves = listOf("Classic"),
        downloadCount = 1250,
        coverUrl = null,
        formats = emptyMap(),
        localFilePath = null
    )


    BookItem(
        book = fakeBook,
        onBookClick = {}
    )

}