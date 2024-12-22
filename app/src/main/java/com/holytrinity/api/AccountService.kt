package com.holytrinity.api

import com.holytrinity.model.Account
import com.holytrinity.model.Curriculum
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Part

interface AccountService {

    @GET("crud-android/trinity/ManageAccounts/getAllAccounts.php")
    fun getAllAccounts(): Call<List<Account>>


    @POST("crud-android/trinity/ManageAccounts/Accounts.php")
    @FormUrlEncoded
    fun addAccount(
        @Field("username") username: String,
        @Field("role_id") roleId: Int,
        @Field("password") password: String,
        @Field("name") name: String
    ): Call<ApiResponse>



}
