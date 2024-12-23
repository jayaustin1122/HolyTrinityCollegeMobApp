package com.holytrinity.model

data class PreEnlistRequest(
    val student_id: String,
    val subject_id: String,
    val enrollment_period_id: String,
    val dept_id: String,
    val course_id: String,
    val curriculum_id: String,
    val level: String
)
