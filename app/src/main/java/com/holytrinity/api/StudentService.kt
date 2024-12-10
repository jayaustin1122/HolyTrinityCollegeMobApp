package com.holytrinity.api

import com.holytrinity.model.Student
import retrofit2.Call
import retrofit2.http.*

interface StudentService {

    @GET("crud-android/getAllstudents.php")
    fun getStudents(@Query("id") studentId: String? = null): Call<Any>
    // Add a new student
    @GET("crud-android/getAllstudents.php")
    fun addStudent(@Body student: Student): Call<Void>

    // Update a student by ID
    @GET("crud-android/getAllstudents.php")
    fun updateStudent(@Query("id") studentId: String, @Body student: Student): Call<Void>

    // Delete a student by ID
    @GET("crud-android/getAllstudents.php")
    fun deleteStudent(@Query("id") studentId: String): Call<Void>
}
