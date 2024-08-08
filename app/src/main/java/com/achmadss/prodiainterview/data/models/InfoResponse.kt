package com.achmadss.prodiainterview.data.models

import com.google.gson.annotations.SerializedName

data class InfoResponse(
    val version: String = "",
    @SerializedName("news_sites")
    val newsSites: List<String> = listOf()
)