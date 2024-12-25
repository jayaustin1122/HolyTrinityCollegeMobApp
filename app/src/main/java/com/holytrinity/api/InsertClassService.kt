package com.holytrinity.api

import com.holytrinity.model.InsertClassResponse
import retrofit2.Call
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Field

interface InsertClassService {

    @FormUrlEncoded
    @POST("crud-android/trinity/insertClasses.php")
    fun insertInClasses(
        @Field("subject_id") subjectId: Int,
        @Field("instructor_user_id") instructorUserId: Int,
        @Field("enrollment_period_id") enrollmentPeriodId: Int,
        @Field("schedule") schedule: String,
        @Field("section") section: String,
        @Field("max_enrollment") maxEnrollment: Int
    ): Call<InsertClassResponse>
}