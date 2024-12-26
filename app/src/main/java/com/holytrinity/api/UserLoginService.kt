package com.holytrinity.api

import com.holytrinity.model.Instructors
import com.holytrinity.model.InstructorsResponse
import com.holytrinity.model.User
import retrofit2.Call
import retrofit2.http.*

interface UserLoginService {


    @GET("crud-android/trinity/getUser.php")
    fun getUser(
        @Query("username") username: String,
        @Query("password") password: String
    ): Call<UserResponse>

    @GET("crud-android/trinity/getUser.php")
    fun getAllInstructors(): Call<InstructorsResponse>
}
