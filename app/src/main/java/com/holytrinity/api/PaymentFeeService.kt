package com.holytrinity.api

import com.holytrinity.model.PaymentFee
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.POST

interface PaymentFeeService {

    @POST("crud-android/trinity/PaymentFee.php")
    fun addPaymentFee(@Body fee: PaymentFee): Call<ApiResponse>

    @GET("crud-android/trinity/PaymentFee.php")
    fun getPaymentFee(@Query("action") action: String): Call<ApiResponse>

    @POST("crud-android/trinity/PaymentFee.php?action=deletePaymentFee")
    fun deletePaymentFee(@Body requestBody: Map<String, Int>): Call<ApiResponse>

    @POST("crud-android/trinity/PaymentFee.php?action=updatePaymentFee")
    fun updatePaymentFee(@Body fee: com.holytrinity.api.PaymentFee): Call<ApiResponse>
}