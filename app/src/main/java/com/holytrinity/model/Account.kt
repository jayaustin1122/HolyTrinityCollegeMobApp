package com.holytrinity.model

data class Account(
    val user_id: Int,
    val role_id: Int,
    val username: String,
    val password_hash: String,
    val name: String,
    val email: String,
    val created_at: String,
    val updated_at: String

)
