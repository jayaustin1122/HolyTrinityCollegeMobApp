package com.holytrinity.api

import com.holytrinity.model.Enrollment
import com.holytrinity.model.EnrollmentResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface EnrollmentService {
    @GET("crud-android/trinity/EnrollmentFlow/flowOne.php")
    fun enrollFlow(
        @Query("curr_id") currId: Int,
        @Query("yearLevel") yearLevel: String,
        @Query("enrollment_period_id") enrollmentPeriodId: Int,
        @Query("student_id") studentId: Int
    ): Call<EnrollmentResponse>
}
