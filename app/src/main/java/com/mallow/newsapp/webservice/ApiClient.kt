package com.mallow.newsapp.webservice

import com.mallow.newsapp.BuildConfig
import com.mallow.newsapp.util.sharedpreference.SharedPrefManager
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class ApiClient @Inject constructor(private val sharedPrefManager: SharedPrefManager) {

    fun getApiClient() = provideRetrofit().create<ApiInterface>()

    private fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.spaceflightnewsapi.net/")
            .addConverterFactory(
                MoshiConverterFactory.create(
                    Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                )
            )
            .client(provideOkHttpClient())
            .build()
    }

    private fun provideOkHttpClient(): OkHttpClient {

        val loggingInterceptor: HttpLoggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClientBuilder = OkHttpClient.Builder()
        okHttpClientBuilder.connectTimeout(60, TimeUnit.SECONDS) //Connection time out set limit
        okHttpClientBuilder.readTimeout(60, TimeUnit.SECONDS)

        if (BuildConfig.DEBUG)
            okHttpClientBuilder.addInterceptor(loggingInterceptor)
        return okHttpClientBuilder.build()

    }

}