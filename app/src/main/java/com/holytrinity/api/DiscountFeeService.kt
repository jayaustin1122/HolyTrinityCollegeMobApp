package com.holytrinity.api

import com.holytrinity.model.DiscountFee
import retrofit2.Call
import retrofit2.http.GET

interface DiscountFeeService {
    @GET("crud-android/getDiscountFee.php")
    fun getDiscountFees(): Call<List<DiscountFee>>
}
