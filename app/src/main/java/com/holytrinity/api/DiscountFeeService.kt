package com.holytrinity.api

import com.holytrinity.model.AssessmentFee
import com.holytrinity.model.DiscountFee
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface DiscountFeeService {

    @POST("crud-android/trinity/DiscountFee.php")
    fun addDiscountFee(@Body fee: DiscountFee): Call<ApiResponse>

    @GET("crud-android/trinity/DiscountFee.php")
    fun getDiscountFees(@Query("action") action: String): Call<ApiResponse>

    @POST("crud-android/trinity/DiscountFee.php?action=deleteDiscountFee")
    fun deleteDiscountFee(@Body requestBody: Map<String, Int>): Call<ApiResponse>

    @POST("crud-android/trinity/DiscountFee.php?action=updateDiscountFee")
    fun updateDiscountFee(@Body fee: com.holytrinity.api.DiscountFee): Call<ApiResponse>
}
