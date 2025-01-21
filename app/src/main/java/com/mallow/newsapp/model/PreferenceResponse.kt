package com.mallow.newsapp.model

data class PreferenceResponse(
    val news_sites: List<String?>? = null,
    val version: String? = null
)