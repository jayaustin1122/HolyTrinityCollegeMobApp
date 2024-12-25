package com.holytrinity.model

data class Subject (
    val subject_id : Int?,
    val name: String,
    val code: String,
    val units: Int,
    val required_hrs: Int?
)
// Adjusted Instructor class
data class Instructors(
    val user_id: String, // Matching the 'user_id' from the response
    val role_id: String, // Matching the 'role_id' from the response
    val username: String, // Matching the 'username' from the response
    val name: String, // Matching the 'name' from the response
    val email: String? // Matching the 'email' from the response
)

// Adjusted InstructorsResponse class to match the 'users' array in the response
data class InstructorsResponse(
    val status: String,
    val users: List<Instructors> // Changed 'instructors' to 'users' to match the response
)
