package com.achmadss.prodiainterview.data.services

import com.achmadss.prodiainterview.data.common.Constants.BASE_URL
import com.achmadss.prodiainterview.data.models.ArticlePagingResponse
import com.achmadss.prodiainterview.data.models.InfoResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ArticleService {

    @GET("articles")
    suspend fun getArticleList(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("news_site") newsSite: String = "", // comma separated values
        @Query("search") searchQuery: String = ""
    ) : Response<ArticlePagingResponse>

    @GET("info")
    suspend fun getInfo() : Response<InfoResponse>

}

fun provideArticleService(): ArticleService {
    val httpLoggingInterceptor = HttpLoggingInterceptor()
        .apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(httpLoggingInterceptor)
        .build()

    return Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ArticleService::class.java)
}