package com.holytrinity.api

import com.holytrinity.model.Student
import com.holytrinity.model.Subject
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface SubjectService {

    @POST("crud-android/trinity/Subject.php")
    fun addSubject(@Body sub: Subject): Call<ApiResponse>

    @GET("crud-android/trinity/Subject.php")
    fun getSubjects(@Query("action") action: String): Call<ApiResponse>

    @POST("crud-android/trinity/Subject.php?action=deleteSubject")
    fun deleteSubject(@Body requestBody: Map<String, Int>): Call<ApiResponse>

    @POST("crud-android/trinity/Subject.php?action=updateSubject")
    fun updateSubject(@Body subject: com.holytrinity.api.Subject): Call<ApiResponse>

    @GET("crud-android/trinity/getAllSubjectsCurriculum.php")
    fun getAllSubjects(): Call<List<Subject>>


}