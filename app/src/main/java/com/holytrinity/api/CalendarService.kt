package com.holytrinity.api

import com.holytrinity.model.AddEventResponse
import com.holytrinity.model.Event
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface CalendarService {
    @POST("crud-android/trinity/add_event.php")
    fun addEvent(@Body event: Event): Call<AddEventResponse>

    @GET("crud-android/trinity/get_all_events.php")
    fun getAllEvents(): Call<AddEventResponse>
}
