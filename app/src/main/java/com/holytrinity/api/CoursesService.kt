package com.holytrinity.api

import com.holytrinity.model.Courses
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface CoursesService {

    @POST("crud-android/trinity/Courses.php")
    fun addCourses(@Body cour: Courses): Call<ApiResponse>

    @GET("crud-android/trinity/Courses.php")
    fun getCourses(@Query("action") action: String): Call<ApiResponse>

    @POST("crud-android/trinity/Courses.php?action=deleteCourses")
    fun deleteCourses(@Body requestBody: Map<String, Int>): Call<ApiResponse>

    @POST("crud-android/trinity/Courses.php?action=updateCourses")
    fun updateCourses(@Body course: com.holytrinity.api.Courses): Call<ApiResponse>
}