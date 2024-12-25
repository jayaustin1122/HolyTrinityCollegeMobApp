package com.holytrinity.api

import com.holytrinity.model.Curriculum
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface CurriculumService {

    @POST("crud-android/trinity/Curriculum.php")
    fun addCurriculum(@Body curr: Curriculum): Call<ApiResponse>

    @GET("crud-android/trinity/Curriculum.php")
    fun getCurriculums(@Query("action") action: String): Call<ApiResponse>

    @POST("crud-android/trinity/Curriculum.php?action=deleteCurriculum")
    fun deleteCurriculum(@Body requestBody: Map<String, Int>): Call<ApiResponse>

    @POST("crud-android/trinity/Curriculum.php?action=updateCurriculum")
    fun updateCurriculum(@Body curriculum: com.holytrinity.api.Curriculum): Call<ApiResponse>

    @GET("crud-android/trinity/Curriculum.php")
    fun getCurriculumSubjects(
        @Query("action") action: String,
        @Query("curriculum_id") curriculumId: Int
    ): Call<ApiResponse>

    @POST("crud-android/trinity/Curriculum.php?action=removeSubjectFromCurriculum")
    fun removeSubjectFromCurriculum(
        @Body requestBody: Map<String, Int> // { "curriculum_id": X, "subject_id": Y }
    ): Call<ApiResponse>

    @GET("crud-android/trinity/Curriculum.php")
    fun getAllSubjects(
        @Query("action") action: String
    ): Call<ApiResponse>

    @POST("crud-android/trinity/Curriculum.php?action=addSubjectToCurriculum")
    fun addSubjectToCurriculum(
        @Body request: AddSubjectRequest
    ): Call<ApiResponse>

}