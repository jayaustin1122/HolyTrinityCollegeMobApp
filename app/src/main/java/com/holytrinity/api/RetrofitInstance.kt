package com.holytrinity.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private const val BASE_URL = "http://lesterintheclouds.com/"

    // Create logging interceptor (optional)
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Create OkHttpClient with the logging interceptor
    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    // Create Retrofit instance
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    // Public method to get Retrofit service (API interface)
    fun <T> create(service: Class<T>): T {
        return retrofit.create(service)
    }
}
