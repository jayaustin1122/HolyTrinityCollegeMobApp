package com.holytrinity.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface UserService {
    @POST("crud-android/trinity/ManageAccounts/ChangePassword.php")
    fun changePassword(@Body request: ChangePasswordRequest): Call<ChangePasswordResponse>
}