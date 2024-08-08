@file:Suppress("UNCHECKED_CAST")

package com.achmadss.prodiainterview.ui.screens.search

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.achmadss.prodiainterview.data.common.Constants
import com.achmadss.prodiainterview.data.common.asFlow
import com.achmadss.prodiainterview.data.common.get
import com.achmadss.prodiainterview.data.common.put
import com.achmadss.prodiainterview.data.models.Article
import com.achmadss.prodiainterview.data.services.ArticleService
import com.achmadss.prodiainterview.ui.screens.paging.ArticlePagingSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class SearchScreenViewModel(
    private val articleService: ArticleService,
    private val sharedPreferences: SharedPreferences,
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val pager = _searchQuery
        .debounce(500L)
        .onEach {
            if (it.isEmpty()) return@onEach
            val recentSearches = (sharedPreferences.get(
                Constants.SharedPrefKeys.RECENT_SEARCHES, listOf<String>()
            ) as List<String>).toMutableList()
            if (!recentSearches.contains(it)) {
                recentSearches.add(it)
                sharedPreferences.put(Constants.SharedPrefKeys.RECENT_SEARCHES, recentSearches.toList())
            }
        }
        .flatMapLatest {
            Pager(
                PagingConfig(10)
            ) { ArticlePagingSource(articleService, title = it, fromSearch = true) }.flow
        }.cachedIn(viewModelScope)

    fun search(query: String) {
        _searchQuery.update { query }
    }

}