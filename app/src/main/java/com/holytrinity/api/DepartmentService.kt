package com.holytrinity.api

import retrofit2.Call
import retrofit2.http.GET

interface DepartmentService {
    @GET("crud-android/trinity/Student Ledger/getAllDepartments.php")
    fun getDepartments(): Call<List<String>>
}