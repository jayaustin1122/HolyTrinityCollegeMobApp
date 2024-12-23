package com.holytrinity.model

data class Fees(
    val fee_id: Int?,
    val title: String?,
    val amount: Double,
    val description: String?,
    val data: List<Fees>?
)

data class ApiResponse(
    val status: String,
    val data: List<Fees>
)
