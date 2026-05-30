package com.stanislavvelykoivan.booksearch.book.presentation.book_search.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
@Composable
fun BookLanguagesFilter(
    selectedLanguages: List<String>,
    onLanguageSelected: (List<String>) -> Unit,
    modifier: Modifier = Modifier
){
    val languages = listOf("en", "fr", "de", "ru", "es", "it")

    FlowRow(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        languages.forEach { language ->
            val isSelected = selectedLanguages.contains(language)
            FilterChip(
                selected = isSelected,
                onClick = {
                    val newList = if (isSelected) {
                        selectedLanguages - language
                    } else {
                        selectedLanguages + language
                    }
                    onLanguageSelected(newList)
                },
                label = { Text(text = language.uppercase()) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BookLanguageFilterPreview() {
    MaterialTheme {
        Surface {
            BookLanguagesFilter(
                selectedLanguages = listOf("en", "fr"),
                onLanguageSelected = {}
            )
        }
    }
}
