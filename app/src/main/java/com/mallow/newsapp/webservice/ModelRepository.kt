package com.mallow.newsapp.webservice

import android.content.Context
import com.mallow.newsapp.model.ArticlesResponse
import com.mallow.newsapp.model.ArticlesResult
import com.mallow.newsapp.model.PreferenceResponse
import com.mallow.newsapp.model.State
import com.mallow.newsapp.state.BaseState
import com.mallow.newsapp.util.sharedpreference.SharedPrefManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject

class ModelRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sharedPrefManager: SharedPrefManager,
    private val apiClient: ApiClient
) {

    fun articlesFeed(offset: Int? = null,searchitem:String? = null,
                     baseFlow: MutableSharedFlow<BaseState>?): Flow<State<ArticlesResponse>> {
        return object : NetworkBoundRepository<ArticlesResponse>(
            baseFlow = baseFlow, sharedPrefManager = sharedPrefManager, context = context
        ) {
            override suspend fun fetchData() = apiClient.getApiClient().articlesFeed(offset,searchitem)
        }.asFlow()
    }

    fun articleDetails(itemid:Int,
                       baseFlow: MutableSharedFlow<BaseState>?): Flow<State<ArticlesResult>> {
        return object : NetworkBoundRepository<ArticlesResult>(
            baseFlow = baseFlow, sharedPrefManager = sharedPrefManager, context = context
        ) {
            override suspend fun fetchData() = apiClient.getApiClient().articleDetails(itemid)
        }.asFlow()
    }

    fun preferenceList(baseFlow: MutableSharedFlow<BaseState>?): Flow<State<PreferenceResponse>> {
        return object : NetworkBoundRepository<PreferenceResponse>(
            baseFlow = baseFlow, sharedPrefManager = sharedPrefManager, context = context
        ) {
            override suspend fun fetchData() = apiClient.getApiClient().preferenceList()
        }.asFlow()
    }
}