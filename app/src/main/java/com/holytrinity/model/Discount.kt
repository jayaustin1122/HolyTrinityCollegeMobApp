package com.holytrinity.model

data class Discount(
    val discount_id: String,
    val code: String,
    val title: String,
    val amount: Double,
    val description: String
)
