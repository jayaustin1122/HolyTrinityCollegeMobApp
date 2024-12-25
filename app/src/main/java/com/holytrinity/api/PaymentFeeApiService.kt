package com.holytrinity.api

import com.holytrinity.model.PaymentFee
import com.holytrinity.model.PaymentRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface PaymentFeeApiService {
    @GET("crud-android/trinity/CashierPaymentTransactions/Payment.php")
    fun getAllFees(): Call<List<PaymentFee>>

    @Headers("Content-Type: application/json")
    @POST("crud-android/trinity/insertToPayments.php")
    fun insertToPayments(
        @Body paymentData: PaymentRequest
    ): Call<PaymentRequest>

}
