package com.achmadss.prodiainterview.data

import com.achmadss.prodiainterview.data.models.ArticleList
import com.achmadss.prodiainterview.data.models.Info
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ArticleService {

    @GET("articles")
    suspend fun getArticleList(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("news_site") newsSite: String = "", // comma separated values
    ) : Response<ArticleList>

    @GET("info")
    suspend fun getInfo() : Response<Info>

}