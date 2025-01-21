package com.mallow.newsapp.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.mallow.newsapp.model.ArticlesResult
import com.mallow.newsapp.model.State
import com.mallow.newsapp.state.ArticleListState
import com.mallow.newsapp.webservice.ModelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArticlesListViewModel @Inject constructor(private val modelRepository: ModelRepository) :
    BaseViewModel<ArticleListState>() {

    var currentOffset = 10 // Tracks current offset
    private val pageSize = 10 // Number of items to load per page

    private val _nextPageAvailable = mutableStateOf(true)
    val nextPageAvailable: MutableState<Boolean> = _nextPageAvailable

    var isLoading = mutableStateOf(false)

    private val _articles = mutableStateListOf<ArticlesResult>() // Holds the articles
    val articles: List<ArticlesResult> get() = _articles // Expose as an immutable list

    private var setArticleListState: ArticleListState = ArticleListState.Init
        set(value) {
            field = value
            setState(setArticleListState)
        }

    fun loadNextPage() {
        if (!_nextPageAvailable.value || isLoading.value) return

        isLoading.value = true
        viewModelScope.launch {
            try {
                articleList(currentOffset) // Load articles with current offset
            } catch (e: Exception) {
                // Handle exceptions (e.g., log or show error)
            } finally {
                isLoading.value = false
            }
        }
    }

    fun articleList(offset: Int? = null,search: String?= null) {
        viewModelScope.launch {
            modelRepository.articlesFeed(
                offset = offset, searchitem = search,
                _baseState
            ).collect {
                when (it) {
                    is State.Success -> {
                        val newArticles = it.data.results
                        when {
                            offset == 0 || search != null -> {
                                _articles.clear()
                                currentOffset = 0
                            }
                        }

                        // Add new articles to the list
                        newArticles?.let { articles ->
                            _articles.addAll(articles)
                            // Update offset for the next page
                            if (offset != null) {
                                currentOffset = offset + articles.size
                            }
                            // Check if more data is available
                            _nextPageAvailable.value = articles.size == pageSize
                        }

                        setArticleListState = ArticleListState.ArticleListSuccessState(it.data)
                    }

                    is State.Error -> {
                        setArticleListState = ArticleListState.ShowMessage(it.detail)
                    }

                    else -> {
                        dismissProgressBar()
                    }
                }
            }
        }
    }

    fun preferenceList() {
        viewModelScope.launch {
            modelRepository.preferenceList(_baseState).collect {
                when (it) {
                    is State.Success -> {
                        setArticleListState = ArticleListState.PreferenceListSuccessState(it.data)
                    }

                    is State.Error -> {
                        setArticleListState = ArticleListState.ShowMessage(it.detail)
                    }

                    else -> {
                        dismissProgressBar()
                    }
                }
            }
        }
    }

    fun articleDetails(articleid: Int?) {
        viewModelScope.launch {
            if (articleid != null) {
                modelRepository.articleDetails(articleid, _baseState).collect {
                    when (it) {
                        is State.Success -> {
                            setArticleListState =
                                ArticleListState.ArticleDetailSuccessState(it.data)
                        }

                        is State.Error -> {
                            setArticleListState = ArticleListState.ShowMessage(it.detail)
                        }

                        else -> {
                            dismissProgressBar()
                        }
                    }
                }
            }
        }
    }

}