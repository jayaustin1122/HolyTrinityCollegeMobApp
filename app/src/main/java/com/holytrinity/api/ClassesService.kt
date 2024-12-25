package com.holytrinity.api
import com.holytrinity.model.ClassResponses
import com.holytrinity.model.Subject
import com.holytrinity.model.SuggestedSchedule
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ClassesService {
    @GET("crud-android/trinity/getAllClasses.php")
    fun getAllClasses(): Call<ClassResponses>

    @GET("crud-android/trinity/getAllClasses.php")
    fun getAllClassesWithSched(
        @Query("inst_id") instId: Int,
        @Query("required_hrs") requiredHrs: Int
    ): Call<SuggestedSchedule>


}
