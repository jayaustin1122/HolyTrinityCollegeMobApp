package com.holytrinity.api


import com.holytrinity.model.Student
import com.holytrinity.model.StudentSolo
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface StudentService {

    @FormUrlEncoded
    @POST("crud-android/trinity/insertStudentLedger.php")
    fun addStudentLedger(
        @Field("student_id") studentId: Int,
        @Field("enrollment_period_id") enrollmentPeriodId: Int,
        @Field("total_due") totalDue: Double,
        @Field("balance") balance: Double
    ): Call<ApiResponse>
    @POST("crud-android/trinity/addBene.php")
    fun addStudentsBene(
        @Query("user_id") benefactorId: Int? = null,
        @Query("student_id") studentId: Int? = null,
        @Query("from") from: String? = null,
        @Query("status") status: String? = null
    ): Call<List<Student>>

    @GET("crud-android/trinity/getAllStudents.php")
    fun getStudents(
        @Query("student_id") studentId: Int? = null,
        @Query("registrationVerified") registrationVerified: Int? = null
    ): Call<List<Student>>
    @GET("crud-android/trinity/getAllStudents2.php")
    fun getStudents2(
        @Query("semester") semester: String? = null,
    ): Call<List<Student>>

        //   getStudents() // No parameters, fetches all students
        //   getStudents(studentId = "12345")  Get a Student by ID:
        //   getStudents(registrationVerified = "1")  Get Students with Registration Verified:
        @GET("crud-android/trinity/getStudentsBeneParent.php")
        fun getStudentsInBeneParent(
            @Query("user_id") userId: Int? = null,
            @Query("from") from: String? = null
        ): Call<List<Student>>


    @GET("crud-android/trinity/getStudent.php")
    fun getStudent(
        @Query("student_id") studentId: String
    ): Call<StudentSolo>


        @GET("crud-android/trinity/saveState.php")
        fun getStudentState(
            @Query("user_id") userId: String
        ): Call<StudentSolo>

    @GET("crud-android/trinity/getParentChild.php")
    fun getParentChild(
        @Query("user_id") userId: String
    ): Call<ParentChildWrapper>


    @GET("crud-android/trinity/getBeneChild.php")
    fun getBeneChild(
        @Query("user_id") userId: String
    ): Call<ParentChildWrapper>


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
        @Part("dept_id") deptId: RequestBody,
        @Part medCert: MultipartBody.Part?,
        @Part("userType") userType: RequestBody,
        @Part("email") email: RequestBody,
        @Part("fullName") fullName: RequestBody,
        @Part("lastName") lastName: RequestBody,
        @Part("firstName") firstName: RequestBody,
        @Part("middleName") middleName: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part("birthdate") birthdate: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part("municipality") municipality: RequestBody,
        @Part("barangay") barangay: RequestBody,
        @Part("curriculum") curriculum: RequestBody,
        @Part("entryPeriod") entryPeriod: RequestBody,
        @Part("studentID") studentID: RequestBody,
        @Part("createdAt") createdAt: RequestBody,

        // File parts





    ): Call<Void>


}
