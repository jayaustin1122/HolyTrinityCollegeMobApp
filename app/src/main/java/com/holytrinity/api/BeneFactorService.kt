package com.holytrinity.api

import com.holytrinity.model.BeneResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface BeneFactorService {

    @GET("crud-android/trinity/getBenefactor.php")
    fun getBenefactorName(@Query("student_id") studentId: String?): Call<BeneResponse>?
}
