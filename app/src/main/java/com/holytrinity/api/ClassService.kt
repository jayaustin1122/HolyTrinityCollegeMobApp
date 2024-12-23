package com.holytrinity.api

import com.holytrinity.model.ClassesResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ClassService {
    @GET("get_instructor_classes.php")
    fun getInstructorClasses(@Query("instructor_user_id") instructorUserId: Int): Call<ClassesResponse>
}