package com.holytrinity.model
data class PaymentRequest(
    val student_id: Int,
    val amount: Int,
    val mode_of_transaction: String?,
    val benefactor_id: Int?,
    val discount_id: Int,

)
