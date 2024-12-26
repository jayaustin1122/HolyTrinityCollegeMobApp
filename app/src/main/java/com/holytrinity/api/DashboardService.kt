package com.holytrinity.api

import com.holytrinity.model.DashboardStats
import retrofit2.Call
import retrofit2.http.GET

interface DashboardService {
    @GET("crud-android/trinity/getDashboardStats.php")
    fun getDashboardStats(): Call<DashboardStats>
}
