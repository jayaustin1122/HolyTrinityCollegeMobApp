package com.holytrinity.model

data class Class(
    val class_id: Int,
    val subject_id: Int,
    val instructor_user_id: Int,
    val schedule: String,
    val section: String,
    val code: String,
    val name: String,
    val units: Int
)

data class ClassesResponse(
    val success: Boolean,
    val classes: List<Class>
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