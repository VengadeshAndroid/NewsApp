package com.mallow.newsapp.state

import com.mallow.newsapp.model.ArticlesResponse
import com.mallow.newsapp.model.ArticlesResult
import com.mallow.newsapp.model.PreferenceResponse


sealed class ArticleListState {

    object Init : ArticleListState()

    data class ArticleListSuccessState(val data : ArticlesResponse) : ArticleListState()

    data class ArticleDetailSuccessState(val data : ArticlesResult) : ArticleListState()

    data class PreferenceListSuccessState(val data : PreferenceResponse) : ArticleListState()

    data class ShowMessage(val msg: String) : ArticleListState()
}