package com.holytrinity.api

data class ApiResponse(
    val status: String,
    val message: String,
    val data: List<SubFee>,
    val assessment: List<AssessmentFee>,
    val discount: List<DiscountFee>,
    val payment: List<PaymentFee>
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

