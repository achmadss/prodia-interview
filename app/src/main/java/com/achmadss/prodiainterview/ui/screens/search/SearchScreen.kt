package com.achmadss.prodiainterview.ui.screens.search

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.achmadss.prodiainterview.data.common.Constants
import com.achmadss.prodiainterview.data.common.asFlow
import com.achmadss.prodiainterview.ui.components.LazyPagingArticleItems
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.koin.androidx.compose.koinViewModel

object SearchScreen: Screen {
    private fun readResolve(): Any = SearchScreen

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current ?: throw Exception("Navigator is null")
        val context = LocalContext.current
        val focusRequester = remember { FocusRequester() }
        val keyboardController = LocalSoftwareKeyboardController.current

        val viewModel = koinViewModel<SearchScreenViewModel>()
        val searchQuery = viewModel.searchQuery.collectAsState().value
        val pagingData = viewModel.pager.collectAsLazyPagingItems()
        val sharedPreferences by lazy {
            context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE)
        }
        val recentSearches = sharedPreferences.asFlow(
            Constants.SharedPrefKeys.RECENT_SEARCHES, listOf<String>()
        ).collectAsState(initial = listOf())

        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    modifier = Modifier,
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    ),
                    title = {
                        OutlinedTextField(
                            modifier = Modifier
                                .focusRequester(focusRequester)
                                .fillMaxWidth(),
                            value = searchQuery,
                            onValueChange = {
                                viewModel.search(it)
                            },
                            textStyle = MaterialTheme.typography.bodyMedium,
                            placeholder = {
                                Text(
                                    text = "Search...",
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent
                            ),
                            keyboardOptions = KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Search
                            ),
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(
                                        onClick = {
                                            viewModel.search("")
                                            focusRequester.requestFocus()
                                            keyboardController?.show()
                                        }
                                    ) {
                                        Icon(imageVector = Icons.Default.Close, contentDescription = "")
                                    }
                                }
                            },
                            singleLine = true
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "")
                        }
                    },
                )
            }
        ) { contentPadding ->
            if (searchQuery.isEmpty()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(contentPadding),
                ) {
                    recentSearches.value?.let {
                        item {
                            Text(
                                modifier = Modifier.padding(
                                    start = 16.dp,
                                    end = 16.dp,
                                    bottom = 8.dp
                                ),
                                text = "Recent Searches",
                                color = Color.LightGray,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                        items(it) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.search(it)
                                        keyboardController?.hide()
                                    }
                                    .padding(16.dp),
                            ) {
                                Icon(imageVector = Icons.Filled.History, contentDescription = "")
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    modifier = Modifier.weight(1f),
                                    text = it,
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(imageVector = Icons.Filled.ArrowOutward, contentDescription = "")
                            }
                        }
                    }
                }
            } else {
                Box(modifier = Modifier
                    .padding(contentPadding)
                    .padding(16.dp)
                ) {
                    LazyPagingArticleItems(pagingData)
                }
            }
        }
    }

}