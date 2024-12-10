package com.holytrinity.model



data class DiscountFee(
    val id: Int,
    val title: String,
    val code: String,
    val amount: Double,
    val description: String
)
