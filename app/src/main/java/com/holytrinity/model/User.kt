package com.holytrinity.model

data class User (
    val user_id: Int,
    val role_id: Int,
    val username: String,
    val password: String,
    val name: String,
    val email: String,
)