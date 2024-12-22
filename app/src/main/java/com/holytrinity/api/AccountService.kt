package com.holytrinity.api

import com.holytrinity.model.Account
import com.holytrinity.model.Curriculum
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AccountService {

    @GET("crud-android/trinity/ManageAccounts/getAllAccounts.php")
    fun getAllAccounts(): Call<List<Account>>

    @POST("crud-android/trinity/ManageAccounts/Accounts.php?action=add_account")
    fun addAccount(@Body account: AccountRequest): Call<ApiResponse>
}
