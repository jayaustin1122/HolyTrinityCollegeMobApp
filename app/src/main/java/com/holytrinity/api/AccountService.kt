package com.holytrinity.api

import com.holytrinity.model.Account
import retrofit2.Call
import retrofit2.http.GET

interface AccountService {
    @GET("crud-android/trinity/getAllAccounts.php")
    fun getAllAccounts(): Call<List<Account>>
}
