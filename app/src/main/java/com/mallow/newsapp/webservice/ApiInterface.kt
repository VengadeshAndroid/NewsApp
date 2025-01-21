package com.mallow.newsapp.webservice

import com.mallow.newsapp.model.ArticlesResponse
import com.mallow.newsapp.model.ArticlesResult
import com.mallow.newsapp.model.PreferenceResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface {

    @GET("v4/articles/")
    suspend fun articlesFeed(@Query("offset") limit: Int? = null,@Query("title_contains") search: String? = null): Response<ArticlesResponse>

    @GET("v4/articles/{id}")
    suspend fun articleDetails(@Path("id") id: Int): Response<ArticlesResult>

    @GET("v4/info/")
    suspend fun preferenceList(): Response<PreferenceResponse>

}