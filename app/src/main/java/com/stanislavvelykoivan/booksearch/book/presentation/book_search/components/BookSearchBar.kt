package com.stanislavvelykoivan.booksearch.book.presentation.book_search.components


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stanislavvelykoivan.booksearch.R
import androidx.compose.ui.graphics.Color
@Composable
fun BookSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
){
    val keyboardController = LocalSoftwareKeyboardController.current

    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        singleLine = true,
        modifier = modifier.fillMaxWidth(),
        colors = TextFieldDefaults.colors(
            focusedTrailingIconColor = Color.Black,
            unfocusedTrailingIconColor = Color.Black
        ),
        placeholder = {
            Text(text = stringResource(id = R.string.search_hint))
        },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search,
            keyboardType = KeyboardType.Text
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearch()
                keyboardController?.hide()
            }
        ),
        trailingIcon = {
                IconButton(onClick = {
                    onSearch()
                    keyboardController?.hide()
                }) {
                    Icon(Icons.Default.Search, contentDescription = stringResource(id = R.string.search_hint))
                }
        },
        shape = RoundedCornerShape(32.dp)

    )
}


@Preview(showBackground = true)
@Composable
fun BookSearchBarPreview() {
    Surface {
        BookSearchBar(
            query = "",
            onQueryChange = {},
            onSearch = {}
        )
    }
}