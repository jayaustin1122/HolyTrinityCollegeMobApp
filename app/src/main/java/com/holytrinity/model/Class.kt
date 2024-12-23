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