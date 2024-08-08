package com.achmadss.prodiainterview.data.models

import com.google.gson.annotations.SerializedName

data class ArticleResponse(
    val id: Long = 0L,
    val title: String = "",
    val url: String = "",
    @SerializedName("image_url")
    val imageUrl: String = "",
    @SerializedName("news_site")
    val newsSite: String = "",
    val summary: String = "",
    @SerializedName("published_at")
    val publishedAt: String = "",
    @SerializedName("updated_at")
    val updatedAt: String = "",
    val featured: Boolean = false,
    val launches: List<Launch> = listOf(),
    val events: List<Event> = listOf(),
)

data class Launch(
    @SerializedName("launch_id")
    val launchId: String = "",
    val provider: String = "",
)

data class Event(
    @SerializedName("event_id")
    val eventId: Long = 0L,
    val provider: String = "",
)

data class ArticlePagingResponse(
    val count: Long = 0L,
    val next: String? = null,
    val previous: String? = null,
    val results: List<ArticleResponse> = listOf(),
)