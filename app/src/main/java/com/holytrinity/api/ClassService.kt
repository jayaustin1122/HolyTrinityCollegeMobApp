package com.holytrinity.api

import com.holytrinity.model.ClassesResponse
import com.holytrinity.model.StudentsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ClassService {
    @GET("crud-android/trinity/Instructor/getInstructorClasses.php")
    fun getInstructorClasses(@Query("instructor_user_id") instructorUserId: Int): Call<ClassesResponse>

    @GET("crud-android/trinity/Instructor/getClassEnrolledStudents.php")
    fun getEnrolledStudents(@Query("class_id") classId: Int): Call<StudentsResponse>
}