package com.holytrinity.model

data class DashboardStats(
    val total_students: Int,
    val admitted_students: Int,
    val pending_students: Int,
    val denied_students: Int,
    val enrolled_students: Int
)

