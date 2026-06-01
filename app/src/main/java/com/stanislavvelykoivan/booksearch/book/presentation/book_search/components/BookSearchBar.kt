package com.stanislavvelykoivan.booksearch.book.presentation.book_search.components


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stanislavvelykoivan.booksearch.R
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.PopupProperties
import com.stanislavvelykoivan.booksearch.core.presentation.OnPrimary
import com.stanislavvelykoivan.booksearch.core.presentation.Primary

@Composable
fun BookSearchBar(
    query: String,
    searchHistory: List<String>,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    var isMenuExpanded by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = query,
            onValueChange = {
                onQueryChange(it)
                isMenuExpanded = it.isNotEmpty()
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { isMenuExpanded = it.isFocused && searchHistory.isNotEmpty() },
            colors = TextFieldDefaults.colors(
                focusedTrailingIconColor = OnPrimary,
                unfocusedTrailingIconColor = Color.Black
            ),
            placeholder = { Text(text = stringResource(id = R.string.search_hint)) },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search,
                keyboardType = KeyboardType.Text
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearch(query)
                    isMenuExpanded = false
                    keyboardController?.hide()
                }
            ),
            trailingIcon = {
                IconButton(onClick = {
                    onSearch(query)
                    isMenuExpanded = false
                    keyboardController?.hide()
                }) {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Primary)
                }
            },
            shape = RoundedCornerShape(32.dp)
        )

        DropdownMenu(
            expanded = isMenuExpanded && searchHistory.isNotEmpty(),
            onDismissRequest = { isMenuExpanded = false },

            properties = PopupProperties(focusable = false),
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            searchHistory.forEach { historyQuery ->
                DropdownMenuItem(
                    text = { Text(text = historyQuery) },
                    onClick = {
                        onQueryChange(historyQuery)
                        onSearch(historyQuery)
                        isMenuExpanded = false
                        keyboardController?.hide()
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null
                        )
                    }
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun BookSearchBarPreview() {
    val mockHistory = listOf("Kotlin Coroutines", "Jetpack Compose", "Java to Kotlin migration")

    Surface {
        BookSearchBar(
            query = "Kot",
            searchHistory = mockHistory,
            onQueryChange = {},
            onSearch = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BookSearchBarEmptyPreview() {
    Surface {
        BookSearchBar(
            query = "",
            searchHistory = emptyList(),
            onQueryChange = {},
            onSearch = {}
        )
    }
}