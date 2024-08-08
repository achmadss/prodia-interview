package com.achmadss.prodiainterview.ui.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.achmadss.prodiainterview.data.common.APICallResult
import com.achmadss.prodiainterview.data.services.ArticleService
import com.achmadss.prodiainterview.data.models.Article
import com.achmadss.prodiainterview.data.common.safeAPICall
import com.achmadss.prodiainterview.ui.screens.paging.ArticlePagingSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeScreenUIState(
    val newsSitesLoading: Boolean = true,
    val newsSitesError: Boolean = false,
    val newsSites: List<String> = listOf(),
    val selectedNewsSite: String = "",
)

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
                _uiState.update { it.copy(newsSitesError = true, newsSitesLoading = false) }
            }
        }
    }

    fun selectNewsSite(newsSite: String) {
        _selectedNewsSite.update { newsSite }
    }


}