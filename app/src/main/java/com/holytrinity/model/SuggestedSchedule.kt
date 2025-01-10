package com.holytrinity.model

data class SuggestedSchedule(
    val status: String,
    val suggested_schedules: List<Schedule>,  // List of Schedule objects
    val remaining_hours: Double,
    val all_available_slots: Map<String, List<String>>
)

data class Schedule(
    val day: String,    // The day of the schedule (e.g., "Monday")
    val time: String    // The time range of the schedule (e.g., "8-11AM")
)
