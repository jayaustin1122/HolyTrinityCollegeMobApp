package com.holytrinity.model
data class SuggestedSchedule(
    val status: String,
    val suggested_schedules: List<String>,
    val remaining_hours: Double,
    val all_available_slots: Map<String, List<String>>
)
