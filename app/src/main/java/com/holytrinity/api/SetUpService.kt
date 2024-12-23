package com.holytrinity.api

import com.holytrinity.model.Enrollment_Period
import com.holytrinity.model.Soa
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SetUpService {

    @GET("crud-android/trinity/setUp.php")
    fun getEnrollmentPeriods(
        @Query("studentId") studentId: String? = null
    ): Call<List<Enrollment_Period>>
}