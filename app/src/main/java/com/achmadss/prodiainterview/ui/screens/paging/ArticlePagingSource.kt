package com.achmadss.prodiainterview.ui.screens.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.achmadss.prodiainterview.data.common.APICallResult
import com.achmadss.prodiainterview.data.services.ArticleService
import com.achmadss.prodiainterview.data.models.ArticleResponse
import com.achmadss.prodiainterview.data.common.safeAPICall


class ArticlePagingSource(
    private val articleService: ArticleService,
    private val newsSites: List<String> = listOf(),
    private val title: String = "",
    private val fromSearch: Boolean = false,
) : PagingSource<Int, ArticleResponse>() {

    override fun getRefreshKey(state: PagingState<Int, ArticleResponse>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ArticleResponse> {
        val nextPageNumber = params.key ?: 1
        val limit = 10
        val offset = nextPageNumber * limit
        return try {
            if (fromSearch && title.isEmpty()) {
                LoadResult.Page(
                    data = listOf(),
                    prevKey = null,
                    nextKey = null
                )
            } else {
                val result = safeAPICall {
                    articleService.getArticleList(
                        limit, offset, newsSites.joinToString(","), title
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
            }
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }

}