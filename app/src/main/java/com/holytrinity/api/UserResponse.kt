package com.holytrinity.api

data class UserResponse(
    val status: String,
    val user: User?
)

data class User(
    val user_id: Int,
    val role_id: Int,
    val username: String,
    val name: String,
    val email: String?
)
