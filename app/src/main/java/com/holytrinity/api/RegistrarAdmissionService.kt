package com.holytrinity.api


import com.holytrinity.model.Student
import com.holytrinity.model.SubjectsModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface RegistrarAdmissionService {

    @GET("crud-android/trinity/getStudentAdmission.php")
    fun getStudentAdmission(
        @Query("student_id") studentID: String
    ): Call<List<Student>>

    @GET("crud-android/trinity/updateStudentAdmission.php")
    fun updateStudentAdmission(
        @Query("student_id") studentId: String,
        @Query("action") action: String // "approve" or "deny"
    ): Call<Void>
}


