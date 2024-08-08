package com.achmadss.prodiainterview.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import cafe.adriel.voyager.navigator.LocalNavigator
import coil.compose.SubcomposeAsyncImage
import com.achmadss.prodiainterview.R
import com.achmadss.prodiainterview.data.models.ArticleResponse
import com.achmadss.prodiainterview.ui.common.rememberResourceBitmapPainter
import com.achmadss.prodiainterview.ui.screens.detail.DetailScreen

@Composable
fun LazyPagingArticleItems(
    pagingData: LazyPagingItems<ArticleResponse>
) {
    val navigator = LocalNavigator.current ?: throw Exception("Navigator is null")
    val state = pagingData.loadState

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        when (state.refresh) {
            is LoadState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = "An error has occurred"
                    )
                }
            }

            is LoadState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            is LoadState.NotLoading -> {
                if (pagingData.itemCount > 0) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        items(pagingData.itemCount) {
                            pagingData[it]?.let {
                                CardItem(
                                    modifier = Modifier
                                        .clickable {
                                            navigator.push(
                                                DetailScreen(
                                                    imageUrl = it.imageUrl,
                                                    title = it.title,
                                                    publishedAt = it.publishedAt,
                                                    summary = it.summary,
                                                )
                                            )
                                        },
                                    imageUrl = it.imageUrl,
                                    title = it.title,
                                )
                            }
                        }
                    }
                } else {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Text(
                            modifier = Modifier.align(Alignment.Center),
                            text = "No Articles"
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun CardItem(
    modifier: Modifier = Modifier,
    imageUrl: String,
    title: String,
) {
    Card(
        modifier = modifier
    ) {
        Column(modifier = Modifier) {
            SubcomposeAsyncImage(
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillWidth,
                model = imageUrl,
                contentDescription = "",
                loading = {
                    Box (
                        modifier = Modifier.padding(32.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(48.dp)
                                .align(Alignment.Center)
                        )
                    }
                },
                error = {
                    Image(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentScale = ContentScale.FillWidth,
                        painter = rememberResourceBitmapPainter(id = R.drawable.cover_error),
                        contentDescription = ""
                    )
                }
            )
            Text(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}
