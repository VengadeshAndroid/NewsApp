package com.mallow.newsapp.state

sealed class BaseState {
    object ShowLoader : BaseState()
    object DismissLoader : BaseState()
    object UnAuthorize : BaseState()
    object ShowNetworkAlert : BaseState()
    data class ShowToast(val msg: String) : BaseState()
}
