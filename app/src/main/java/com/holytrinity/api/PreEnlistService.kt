package com.holytrinity.api


import com.holytrinity.model.PreEnlistRequest
import com.holytrinity.model.SubjectsModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PreEnlistService {
    @GET("crud-android/trinity/EnrollmentFlow/insertPreenlist.php")
    fun insertPreEnlist(
        @Query("student_id") studentId: String,
        @Query("subject_id") subjectId: String,
        @Query("enrollment_period_id") enrollmentPeriodId: String,
        @Query("dept_id") deptId: String,
        @Query("course_id") courseId: String,
        @Query("curriculum_id") curriculumId: String,
        @Query("level") level: String
    ): Call<PreEnlistRequest>
}


