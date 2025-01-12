package com.holytrinity.model



data class ClassesResponse(
    val success: Boolean,
    val classes: List<ClassResponses>
)
data class ClassResponses(
    val class_id: Int,
    val subject_id: Int,
    val instructor_user_id: Int,
    val instructor_name: String,
    val enrollment_period_id: Int,
    val schedule: String,
    val section: String,
    val max_enrollment: Int,
    val code: String,
    val name: String,
    val units: Int,
    val classes: List<ClassResponses>
)

data class StudentItem(
    val student_id: Int,
    val student_name: String,
    val grade: String,
    val remark: String
)

data class StudentsResponse(
    val success: Boolean,
    val students: List<StudentItem>
)
data class InsertClassResponse(
    val status: String? = null,
    val message: String? = null,
    val error: String? = null
)