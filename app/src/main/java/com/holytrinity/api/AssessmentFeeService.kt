package com.holytrinity.api

import com.holytrinity.model.AssessmentFee
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AssessmentFeeService {

    @POST("crud-android/trinity/AssessmentFee.php")
    fun addAssessmentFee(@Body fee: AssessmentFee): Call<ApiResponse> // For adding assessment fee

    @GET("crud-android/trinity/AssessmentFee.php")
    fun getSubFeeData(@Query("action") action: String): Call<ApiResponse>

    @GET("crud-android/trinity/AssessmentFee.php")   // For fetching assessment fee
    fun getAssessmentFees(@Query("action") action: String): Call<ApiResponse>

    @POST("crud-android/trinity/AssessmentFee.php?action=deleteAssessmentFee") // For deleting assessment fee
    fun deleteAssessmentFee(@Body requestBody: Map<String, Int>): Call<ApiResponse>

    @POST("crud-android/trinity/AssessmentFee.php?action=updateAssessmentFee")
    fun updateAssessmentFee(@Body fee: com.holytrinity.api.AssessmentFee): Call<ApiResponse> // For updating assessment fee
}
