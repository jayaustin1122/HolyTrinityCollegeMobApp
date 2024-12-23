package com.holytrinity.api

import com.holytrinity.model.Fees
import retrofit2.Call
import retrofit2.http.GET

interface FeesService {
    @GET("crud-android/trinity/getStudentFees.php")
    fun getStudentFees(): Call<Fees>
}
