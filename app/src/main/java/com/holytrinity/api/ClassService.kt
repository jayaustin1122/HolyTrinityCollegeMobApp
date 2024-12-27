package com.holytrinity.api

import com.holytrinity.model.ClassesResponse
import com.holytrinity.model.StudentItem
import com.holytrinity.model.StudentsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ClassService {
    @GET("crud-android/trinity/Instructor/getInstructorClasses.php")
    fun getInstructorClasses(@Query("instructor_user_id") instructorUserId: Int): Call<ClassesResponse>

    @GET("crud-android/trinity/Instructor/getClassEnrolledStudents.php")
    fun getEnrolledStudents(@Query("class_id") classId: Int): Call<StudentsResponse>

    @GET("crud-android/trinity/Instructor/getStudentInClass.php")
    fun getStudentInClass(
        @Query("student_id") studentId: Int,
        @Query("class_id") classId: Int
    ): Call<StudentItem>


    @GET("crud-android/trinity/Instructor/studentUpdateGrade.php")
    fun updateGradeStudent(
        @Query("student_id") studentId: Int,
        @Query("class_id") classId: Int,
        @Query("grade") grade: Int,
        @Query("remarks") remarks: String,
        @Query("attempt_num") attemptNum: Int = 1,
        @Query("status") status: String = "active"
    ): Call<StudentItem>

}