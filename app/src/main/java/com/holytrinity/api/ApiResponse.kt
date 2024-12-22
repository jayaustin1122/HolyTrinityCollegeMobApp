package com.holytrinity.api

data class ApiResponse(
    val status: String,
    val message: String,
    val data: List<SubFee>,
    val assessment: List<AssessmentFee>,
    val discount: List<DiscountFee>,
    val payment: List<PaymentFee>,

    val courses: List<Courses>,
    val curriculum: List<Curriculum>,
    val subject: List<Subject>
)

data class AccountRequest(
    val role_id: Int,
    val username: String,
    val password_hash: String,
    val name: String,
    val email: String? = null
)


data class Courses (
    val course_id: Int,
    val name: String,
    val code: String,
    val description: String
)

data class Curriculum (
    val curriculum_id: Int,
    val name: String,
    val code: String,
    val description: String
)

data class Subject (
    val subject_id: Int,
    val name: String,
    val code: String,
    val units: Int
)

data class SubFee(
    val id: Int,
    val title: String
)

data class AssessmentFee (
    val id: Int,
    val title: String,
    val amount: Double,
    val description: String,
    val subFee: Int
)

data class DiscountFee(
    val id: Int,
    val title: String,
    val code: String,
    val amount: Double,
    val description: String
)

data class PaymentFee (
    val id: Int,
    val title: String,
    val amount: Double,
    val description: String
)

