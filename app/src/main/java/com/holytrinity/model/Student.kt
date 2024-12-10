package com.holytrinity.model

data class Student(
    val id: String? = null,
    val fname: String,
    val mname: String,
    val lname: String,
    val sex: String,
    val email: String,
    val phone: String,
    val purok: String,
    val birthdate: String,
    val province: String,
    val municipality: String,
    val barangay: String,
    val lrn: String?,
    val level: String,
    val userId: String,
    val studentId: String,
    val parent: String?,
    val benefactor: String?
)
