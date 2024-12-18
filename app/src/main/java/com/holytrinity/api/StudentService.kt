package com.holytrinity.api

import com.holytrinity.model.Student
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface StudentService {

    @GET("crud-android/trinity/getAllStudents.php")
    fun getStudents(@Query("studentID") studentId: String? = null): Call<List<Student>>

    // Add a new student
    @POST("crud-android/trinity/addStudent.php")
    @Multipart
    fun uploadStudentData(
        @Part("lrn") lrn: RequestBody,
        @Part("attended") attended: RequestBody,
        @Part form137: MultipartBody.Part?,
        @Part diploma: MultipartBody.Part?,
        @Part tor: MultipartBody.Part?,
        @Part coh: MultipartBody.Part?,
        @Part esc: MultipartBody.Part?,
        @Part("userName") userName: RequestBody,
        @Part("password") password: RequestBody,
        @Part baptismal: MultipartBody.Part?,
        @Part confirmCert: MultipartBody.Part?,
        @Part nso: MultipartBody.Part?,
        @Part marriageCert: MultipartBody.Part?,
        @Part brgyCert: MultipartBody.Part?,
        @Part indigency: MultipartBody.Part?,
        @Part birForm: MultipartBody.Part?,
        @Part recommLetter: MultipartBody.Part?,
        @Part("parish") parish: RequestBody,
        @Part medCert: MultipartBody.Part?,
        @Part("userType") userType: RequestBody,
        @Part("email") email: RequestBody,
        @Part("fullName") fullName: RequestBody,
        @Part("lastName") lastName: RequestBody,
        @Part("firstName") firstName: RequestBody,
        @Part("middleName") middleName: RequestBody,
        @Part("sex") sex: RequestBody,
        @Part("dateOfBirth") dateOfBirth: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part("municipality") municipality: RequestBody,
        @Part("barangay") barangay: RequestBody,
        @Part("curriculum") curriculum: RequestBody,
        @Part("entryPeriod") entryPeriod: RequestBody,
        @Part("studentID") studentID: RequestBody,
        @Part("createdAt") createdAt: RequestBody,

        // File parts





    ): Call<Void>

    // Update a student by ID
    @POST("crud-android/trinity/updateStudent.php")
    fun updateStudent(@Query("id") studentId: String, @Body student: Student): Call<Void>
}
