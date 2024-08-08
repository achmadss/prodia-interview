package com.achmadss.prodiainterview.data.models

data class ArticleList(
    val count: Long = 0L,
    val next: String? = null,
    val previous: String? = null,
    val results: List<Article> = listOf(),
)