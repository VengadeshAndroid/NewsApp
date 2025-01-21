package com.mallow.newsapp.util

interface Constants {


    object SharedKey {
        const val TOKEN = "preference"
        var COUNT = "count"
        var NEXT = "next"
    }

    object InternalHttpCode {
        const val SUCCESS = 200
        const val CREATED = 201
        const val FAILURE_CODE = 404
        const val BAD_REQUEST_CODE = 400
        const val UNAUTHORIZED_CODE = 401
        const val INTERNAL_SERVER_ERROR_CODE = 500
        const val TIMEOUT_CODE = 504
        const val INTERNAL_SERVER_ERROR =
            "Our server is under maintenance. We will resolve shortly!"
    }


}

