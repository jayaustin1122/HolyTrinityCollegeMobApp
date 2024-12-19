package com.holytrinity.main
data class Event(
    val event_id: Int,
    val event_name: String,
    val event_date: String,
    val description: String
)


data class AddEventResponse(
    val success: String?,
    val error: String?,
    val events: List<Event>?
)

