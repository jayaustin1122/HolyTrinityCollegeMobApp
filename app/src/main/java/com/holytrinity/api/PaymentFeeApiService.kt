package com.holytrinity.api

import com.holytrinity.model.PaymentFee
import retrofit2.Call
import retrofit2.http.GET

interface PaymentFeeApiService {
    @GET("crud-android/trinity/CashierPaymentTransactions/Payment.php")
    fun getAllFees(): Call<List<PaymentFee>>
}
