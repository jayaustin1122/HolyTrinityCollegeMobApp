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
import retrofit2.http.Query

interface PaymentFeeApiService {
    @GET("crud-android/trinity/CashierPaymentTransactions/Payment.php")
    fun getAllFees(): Call<List<PaymentFee>>

    @Headers("Content-Type: application/json")
    @POST("crud-android/trinity/insertToPayments.php")
    fun insertToPayments(
        @Body paymentData: PaymentRequest
    ): Call<PaymentRequest>

    @Headers("Content-Type: application/json")
    @POST("crud-android/trinity/insertToPaymentNotAssessment.php")
    fun insertToPaymentss(
        @Query("student_id") student_id: Int,
        @Query("amount") amount: Int,
        @Query("mode_of_transaction") mode_of_transaction: String,
        @Query("benefactor_id") benefactor_id: Int?,
        @Query("discount_id") discount_id: Int
    ): Call<PaymentRequest>
}
