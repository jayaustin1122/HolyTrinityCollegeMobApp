package com.holytrinity.model

data class Subject (
    val subject_id: Int?,
    val name: String,
    val code: String,
    val units: Int,
    val required_hrs: Int?
)


data class Instructors(
    val user_id: String,
    val role_id: String,
    val username: String,
    val name: String,
    val email: String?
)

data class InstructorsResponse(
    val status: String,
    val instructors: List<Instructors>
)
