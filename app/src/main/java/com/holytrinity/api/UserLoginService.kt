package com.holytrinity.api

import com.holytrinity.model.Student
import com.holytrinity.model.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface UserLoginService {


    @GET("crud-android/trinity/getUser.php")
    fun getUser(
        @Query("username") username: String,
        @Query("password") password: String
    ): Call<List<User>>


}
