package com.mallow.newsapp.model

data class ArticlesResponse(
    val count: Int? = null,
    val next: String? = null,
    val previous: String? = null,
    val results: List<ArticlesResult>? = null
)

data class ArticlesResult(
    val events: List<Event?>? = null,
    val featured: Boolean? = null,
    val id: Int? = null,
    val image_url: String? = null,
    val launches: List<Launche?>? = null,
    val news_site: String? = null,
    val published_at: String? = null,
    val summary: String? = null,
    val title: String? = null,
    val updated_at: String? = null,
    val url: String? = null
)

data class Event(
    val event_id: Int? = null,
    val provider: String? = null
)

data class Launche(
    val launch_id: String? = null,
    val provider: String? = null
)
