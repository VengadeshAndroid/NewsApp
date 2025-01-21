package com.mallow.newsapp.webservice

import android.content.Context
import android.os.Looper
import com.google.gson.Gson
import com.mallow.newsapp.model.ErrorResponse
import com.mallow.newsapp.model.State
import com.mallow.newsapp.state.BaseState
import com.mallow.newsapp.util.Constants
import com.mallow.newsapp.util.Constants.InternalHttpCode.INTERNAL_SERVER_ERROR
import com.mallow.newsapp.util.hasNetworkConnection
import com.mallow.newsapp.util.sharedpreference.SharedPrefManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import retrofit2.Response
import timber.log.Timber
import java.io.IOException


/**
 * A repository which provides resource from local database as well as remote end point.
 *
 * [RESULT] represents the type for database.
 * [REQUEST] represents the type for network.
 */

abstract class NetworkBoundRepository<RESULT>(
    private val baseFlow: MutableSharedFlow<BaseState>? = null,
    private val isShowProgress: Boolean = true,
    private val isShowErrorToast: Boolean = true,
    private val sharedPrefManager: SharedPrefManager,
    private val context: Context
) {

    fun asFlow() = flow<State<RESULT>> {

        if (context.hasNetworkConnection()) {

            if (isShowProgress) {
                baseFlow?.emit(BaseState.ShowLoader)
            }

            val apiResponse: Response<RESULT>

            // Parse body
            val remotePosts: RESULT?

            withContext(Dispatchers.IO) {
                apiResponse = fetchData()
                remotePosts = apiResponse.body()
            }

            if (isShowProgress)
                baseFlow?.emit(BaseState.DismissLoader)

            // Check for response validation
            if (apiResponse.isSuccessful && remotePosts != null) {
                // Save posts into the persistence storage
                if (apiResponse.code() == Constants.InternalHttpCode.SUCCESS || apiResponse.code() == Constants.InternalHttpCode.CREATED)
                    emit(State.success(remotePosts))
                else {
                    emit(State.error(apiResponse.message()))
                    if (isShowErrorToast)
                        baseFlow?.emit(BaseState.ShowToast(apiResponse.message()))
                }
            } else if (apiResponse.code() == Constants.InternalHttpCode.UNAUTHORIZED_CODE) {
                sharedPrefManager.clearData()
                baseFlow?.emit(BaseState.UnAuthorize)
            } else {
                // Something went wrong! Emit Error state.
                val error: ErrorResponse? =
                    apiResponse.errorBody()?.string()?.let {
                        getObjectFromJsonString(
                            it,
                            ErrorResponse::class.java
                        )
                    }
                error?.detail?.let {
                    if (isShowErrorToast)
                        baseFlow?.emit(BaseState.ShowToast(it))
                    emit(State.error(it))
                }
            }

        } else {
            // Show Network Settings
            baseFlow?.emit(BaseState.ShowNetworkAlert)
        }

    }.catch { e ->
        // Exception occurred! Emit error
        withContext(Dispatchers.Main) {
            Timber.d("scope===network-->${Looper.myLooper() == Looper.getMainLooper()}")
            if (isShowProgress)
                baseFlow?.emit(BaseState.DismissLoader)
            emit(State.error(INTERNAL_SERVER_ERROR))
            baseFlow?.emit(BaseState.ShowToast(INTERNAL_SERVER_ERROR))
            e.printStackTrace()
            Timber.d("scope===network-->${e.message}")
        }
    }


    protected abstract suspend fun fetchData(): Response<RESULT>


    private fun <T> getObjectFromJsonString(jsonString: String, classType: Class<T>): T? {
        try {
            val gson = Gson()
            return gson.fromJson(jsonString, classType)
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }

        return null
    }
}
