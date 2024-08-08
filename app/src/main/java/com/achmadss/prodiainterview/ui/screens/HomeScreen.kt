package com.achmadss.prodiainterview.ui.screens

import android.content.res.Resources
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.achmadss.prodiainterview.R
import org.koin.androidx.compose.koinViewModel

object HomeScreen : Screen {
    private fun readResolve(): Any = HomeScreen

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {

        val viewModel = koinViewModel<HomeScreenViewModel>()
        val navigator = LocalNavigator.current ?: throw Exception("Navigator is null")

        val uiState = viewModel.uiState.collectAsState().value
        val selectedNewsSite = viewModel.selectedNewsSite.collectAsState().value
        val pagingData = viewModel.pager.collectAsLazyPagingItems()
        val pagingDataState = pagingData.loadState

        var expanded by remember { mutableStateOf(false) }
        val iconFilter = if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown

        Log.e("ASD", "$expanded")

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    modifier = Modifier,
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    ),
                    title = {
                        Text(
                            text = "Space Flight Articles",
                            style = MaterialTheme.typography.titleLarge,
                        )
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                navigator.push(SearchScreen)
                            }
                        ) {
                            Icon(imageVector = Icons.Default.Search, contentDescription = "")
                        }
                    }
                )
            }
        ) { contentPadding ->

            Column(
                modifier = Modifier
                    .padding(contentPadding)
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = !expanded },
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, color = Color.Gray)
                            .padding(16.dp),
                    ) {
                        Text(
                            modifier = Modifier.weight(1f),
                            text = selectedNewsSite.ifEmpty { "Select news site" }
                        )
                        Icon(
                            imageVector = iconFilter,
                            contentDescription = ""
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    uiState.newsSites.forEach {
                        DropdownMenuItem(
                            text = {
                                Text(text = it)
                            },
                            onClick = {
                                expanded = false
                                viewModel.selectNewsSite(it)
                            }
                        )
                    }
                }
                when(pagingDataState.refresh) {
                    is LoadState.Error -> {
                        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                            Text(text = "An error has occurred")
                        }
                    }
                    is LoadState.Loading -> {
                        Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                            CircularProgressIndicator(modifier = Modifier
                                .size(48.dp)
                                .align(Alignment.Center))
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
                                    text = "No Articles from $selectedNewsSite"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun rememberResourceBitmapPainter(@DrawableRes id: Int): BitmapPainter {
    val context = LocalContext.current
    return remember(id) {
        val drawable = ContextCompat.getDrawable(context, id)
            ?: throw Resources.NotFoundException()
        BitmapPainter(drawable.toBitmap().asImageBitmap())
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

