package com.holytrinity.api


import com.holytrinity.model.SubjectsModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SubjectsService {
    @GET("crud-android/trinity/getAllSubjectsCurriculum.php")
    fun getAllSubjectsCurriculum(
        @Query("recommended_year_level") yearLevel: String,
        @Query("curriculum_id") curriculumId: String
    ): Call<List<SubjectsModel>>
}


