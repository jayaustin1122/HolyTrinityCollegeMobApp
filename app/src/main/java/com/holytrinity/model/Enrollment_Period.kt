package com.holytrinity.model

data class Enrollment_Period(
    val enrollment_period_id: Int,
    val year: Int,
    val semester: String,
    val start_date: String,
    val end_date: String
)