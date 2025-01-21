package com.mallow.newsapp.viewmodel

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mallow.newsapp.state.BaseState
import com.mallow.newsapp.util.sharedpreference.SharedPrefManager
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@ViewModelScoped
abstract class BaseViewModel<State> : ViewModel() {

    val showToastFlow = snapshotFlow { "" }

    private val _viewState: MutableSharedFlow<State> by lazy { MutableSharedFlow() }
    val viewState: SharedFlow<State> get() = _viewState.asSharedFlow()

    protected val _baseState: MutableSharedFlow<BaseState> by lazy { MutableSharedFlow() }
    val baseState: SharedFlow<BaseState> get() = _baseState.asSharedFlow()

    @Inject
    lateinit var sharedPrefManager: SharedPrefManager


    protected fun setState(newState: State) {
        viewModelScope.launch {
            _viewState.emit(newState)
        }
    }

    fun getSharedData() = "Test"

    fun showProgressBar() {
        viewModelScope.launch {
            _baseState.emit(BaseState.ShowLoader)
        }
    }

    fun dismissProgressBar() {
        viewModelScope.launch {
            _baseState.emit(BaseState.DismissLoader)
        }
    }

    fun showToast(msg: String) {
        viewModelScope.launch {
            _baseState.emit(BaseState.ShowToast(msg))
        }
    }
}