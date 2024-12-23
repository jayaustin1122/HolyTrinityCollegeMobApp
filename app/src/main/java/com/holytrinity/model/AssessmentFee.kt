package com.holytrinity.model

data class AssessmentFee (
    val title: String,
    val amount: Double,
    val description: String,
    val subFee: Int?
)