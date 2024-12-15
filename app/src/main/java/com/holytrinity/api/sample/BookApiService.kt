package com.holytrinity.api.sample

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface BookApiService {


    @Multipart
    @POST("crud-android/trinity/sample.php")
    fun uploadBook(
        @Part("book_title") bookTitle: RequestBody,
        @Part("content") content: RequestBody,
        @Part cover_image: MultipartBody.Part,
        @Part file: MultipartBody.Part
    ): Call<BookResponse>

    @GET("crud-android/trinity/sample.php")
    fun getBooks(): Call<BooksResponse>
}