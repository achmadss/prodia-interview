package com.achmadss.prodiainterview.ui.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.cachedIn
import com.achmadss.prodiainterview.data.APICallResult
import com.achmadss.prodiainterview.data.ArticleService
import com.achmadss.prodiainterview.data.models.Article
import com.achmadss.prodiainterview.data.safeAPICall
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeScreenUIState(
    val loading: Boolean = true,
    val error: Boolean = false,
    val newsSitesLoading: Boolean = true,
    val newsSitesError: Boolean = false,
    val newsSites: List<String> = listOf(),
    val selectedNewsSite: String = "",
    val articles: List<HomeScreenData> = listOf()
)

data class HomeScreenData(
    val imageUrl: String = "",
    val title: String = "",
    val summary: String = "",
)

class ArticlePagingSource(
    private val articleService: ArticleService,
    private val newsSites: List<String> = listOf(),
) : PagingSource<Int, Article>() {

    override fun getRefreshKey(state: PagingState<Int, Article>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Article> {
        val nextPageNumber = params.key ?: 1
        val limit = 10
        val offset = nextPageNumber * limit
        return try {
            val result = safeAPICall {
                articleService.getArticleList(
                    limit, offset, newsSites.joinToString(",")
                )
            }
            when (result) {
                is APICallResult.Success -> {
                    LoadResult.Page(
                        data = result.data.results,
                        prevKey = if (nextPageNumber == 1) null else nextPageNumber - 1,
                        nextKey = if (result.data.next == null) null else nextPageNumber + 1
                    )
                }

                is APICallResult.Error -> {
                    LoadResult.Error(result.error)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }

}

class HomeScreenViewModel(
    private val articleService: ArticleService,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeScreenUIState())
    val uiState = _uiState.asStateFlow()

    private val _selectedNewsSite = MutableStateFlow("")
    val selectedNewsSite = _selectedNewsSite.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val pager = _selectedNewsSite.flatMapLatest {
        Pager(
            PagingConfig(10)
        ) { ArticlePagingSource(articleService, listOf(it)) }.flow
    }.cachedIn(viewModelScope)

    init {
        getInfo()
    }

    private fun getInfo() = viewModelScope.launch {
        val result = safeAPICall { articleService.getInfo() }
        when (result) {
            is APICallResult.Success -> {
                _uiState.update {
                    it.copy(
                        newsSites = result.data.newsSites,
                        newsSitesLoading = false
                    )
                }
            }

            is APICallResult.Error -> {
                Log.e("ASD", result.error.message ?: "Unknown Error")
                _uiState.update { it.copy(newsSitesError = true) }
            }
        }
    }

    fun selectNewsSite(newsSite: String) {
        _selectedNewsSite.update { newsSite }
    }


}