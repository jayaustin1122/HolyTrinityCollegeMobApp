package com.holytrinity.api

import com.holytrinity.model.Discount
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface DiscountService {
    @GET("crud-android/trinity/getAllDiscounts.php")
    fun getAllDiscounts(): Call<List<Discount>>
}
