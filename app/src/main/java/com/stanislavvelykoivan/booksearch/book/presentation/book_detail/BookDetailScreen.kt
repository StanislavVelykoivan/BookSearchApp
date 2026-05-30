package com.stanislavvelykoivan.booksearch.book.presentation.book_detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.stanislavvelykoivan.booksearch.R
import com.stanislavvelykoivan.booksearch.book.presentation.book_detail.components.Chip
import com.stanislavvelykoivan.booksearch.book.presentation.book_detail.components.TagList
import com.stanislavvelykoivan.booksearch.book.presentation.book_detail.components.Titled
import com.stanislavvelykoivan.booksearch.core.presentation.OnBackground
import com.stanislavvelykoivan.booksearch.core.presentation.Primary
import com.stanislavvelykoivan.booksearch.core.presentation.Tertiary
import org.koin.androidx.compose.koinViewModel

@Composable
fun BookDetailScreenRoot(
    bookId: Long, viewModel: BookDetailViewModel = koinViewModel(), modifier: Modifier = Modifier
) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    BookDetailScreen(
        bookId = bookId, state = state, onAction = { action ->
            viewModel.onAction(action)
        }, modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookDetailScreen(
    bookId: Long,
    state: BookDetailState,
    onAction: (BookDetailAction) -> Unit,
    modifier: Modifier = Modifier
) {

    var showBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(OnBackground),
        contentAlignment = Alignment.Center,
    ) {
        when {
            state.isLoading -> {
                CircularProgressIndicator()
            }

            state.error != null -> {
                Text(
                    text = "Error: ${state.error.asString()}",
                    color = MaterialTheme.colorScheme.error
                )
            }

            state.book != null -> {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        text = state.book.title,
                        style = MaterialTheme.typography.headlineMedium,
                        color = Primary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    AsyncImage(
                        model = state.book.coverUrl,
                        contentDescription = state.book.title,
                        modifier = Modifier.size(300.dp),
                        contentScale = ContentScale.Fit,
                        error = painterResource(id = R.drawable.book_foreground),
                        placeholder = painterResource(id = R.drawable.book_foreground)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (state.book.authors.isNotEmpty()) {

                        Titled(
                            title = "Authors",
                            modifier = Modifier.padding(vertical = 8.dp),
                        ) {

                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                state.book.authors.forEach { author ->
                                    Text(
                                        text = author.name
                                    )

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(
                                            16.dp, Alignment.CenterHorizontally
                                        )
                                    ) {
                                        author.birthYear?.let {
                                            Text(
                                                text = "Born: 👶 $it",
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                        author.deathYear?.let {
                                            Text(
                                                text = "Died: 🕊️ $it",
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        }
                                    }
                                }

                            }
                        }

                    }


                    if (state.book.languages.isNotEmpty()) {
                        TagList(
                            title = "Languages",
                            items = state.book.languages.map { it.uppercase() })
                    }


                    if (state.book.subjects.isNotEmpty()) {
                        TagList(
                            title = "Subjects", items = state.book.subjects
                        )
                    }

                    if (state.book.bookshelves.isNotEmpty()) {
                        TagList(
                            title = "Bookshelves", items = state.book.bookshelves
                        )
                    }

                    Text(text = "Download count: ${state.book.downloadCount}")

                    Spacer(modifier = Modifier.height(16.dp))
                    if (!state.isBookSaved) {
                        Button(
                            onClick = { showBottomSheet = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Tertiary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = "Download"
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Download Book")
                        }
                    }



                    if (showBottomSheet) {
                        ModalBottomSheet(
                            onDismissRequest = { showBottomSheet = false },
                            sheetState = sheetState
                        ) {

                            Column(modifier = Modifier
                                .padding(16.dp)
                                .navigationBarsPadding()) {
                                Text("Choose formate:", style = MaterialTheme.typography.titleLarge)
                                Spacer(modifier = Modifier.height(16.dp))

                                state.book.formats.forEach { (label, url) ->
                                    ListItem(
                                        headlineContent = { Text(label) },
                                        leadingContent = { Icon(Icons.Default.FileDownload, null) },
                                        modifier = Modifier.clickable {
//                                            onAction(BookDetailAction.DownloadFile(url))
                                            showBottomSheet = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}