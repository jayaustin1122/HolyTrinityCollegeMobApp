package com.holytrinity.api

import com.holytrinity.model.Soa
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SoaService {

    @GET("crud-android/trinity/getAllSoa.php")
    fun getAllSoa(@Query("studentID") studentId: String? = null): Call<List<Soa>>


    @GET("crud-android/trinity/getAllSoa.php")
    fun getAllSoaByUserID(@Query("studentID") studentId: String? = null): Call<List<Soa>>
}