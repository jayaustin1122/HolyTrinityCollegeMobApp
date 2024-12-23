package com.holytrinity.model

data class StudentLedgers(
    val student_id: Int,
    val enrollment_period_id: Int,
    val total_due: Double,
    val balance: Double
)
