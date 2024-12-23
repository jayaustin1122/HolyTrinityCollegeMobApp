package com.holytrinity.api

import com.holytrinity.model.CurriculumSubjectsResponse
import com.holytrinity.model.PrerequisitesResponse
import com.holytrinity.model.SubjectsResponse
import retrofit2.Response
import retrofit2.http.GET

interface PrerequisiteService {

    @GET("crud-android/trinity/Pre-Requisites.php?action=getPrerequisites")
    suspend fun getPrerequisites(): Response<PrerequisitesResponse>

    @GET("crud-android/trinity/Pre-Requisites.php?action=getSubjects")
    suspend fun getSubjects(): Response<SubjectsResponse>

    @GET("crud-android/trinity/Pre-Requisites.php?action=getCurriculumSubjects")
    suspend fun getCurriculumSubjects(): Response<CurriculumSubjectsResponse>
}
