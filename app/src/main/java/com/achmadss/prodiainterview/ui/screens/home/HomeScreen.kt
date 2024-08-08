package com.achmadss.prodiainterview.ui.screens.home

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import coil.compose.SubcomposeAsyncImage
import com.achmadss.prodiainterview.R
import com.achmadss.prodiainterview.ui.components.LazyPagingArticleItems
import com.achmadss.prodiainterview.ui.screens.search.SearchScreen
import com.achmadss.prodiainterview.ui.screens.detail.DetailScreen
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

        var expanded by remember { mutableStateOf(false) }
        val iconFilter = if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown

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
                            text = "Articles",
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
                        .fillMaxWidth(),
                ) {
                    if (uiState.newsSitesLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expanded = !expanded }
                                .border(1.dp, color = Color.Gray)
                                .padding(16.dp),
                        ) {
                            Text(
                                modifier = Modifier.weight(1f),
                                text = selectedNewsSite.ifEmpty { "Filter by News Site" }
                            )
                            Icon(
                                imageVector = iconFilter,
                                contentDescription = ""
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(text = "All")
                        },
                        onClick = {
                            expanded = false
                            viewModel.selectNewsSite("")
                        }
                    )
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
                LazyPagingArticleItems(pagingData)
            }
        }
    }
}
