package com.holytrinity.users.student.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.holytrinity.R
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.api.StudentService
import com.holytrinity.databinding.FragmentStudentProfileBinding
import com.holytrinity.model.StudentSolo
import com.holytrinity.util.UserPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class StudentProfileFragment : Fragment() {
    private lateinit var binding : FragmentStudentProfileBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStudentProfileBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userId = UserPreferences.getUserId(requireContext())
        getStudentInfo(userId.toString())
        binding.toolbarBackButton.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("selectedFragmentId", null ?: R.id.nav_dashboard_student)
            }
            findNavController().navigate(R.id.studentDrawerHolderFragment, bundle)
        }
    }
    fun getStudentInfo(userId: String) {
        val studentService = RetrofitInstance.create(StudentService::class.java)
        studentService.getStudentState(userId).enqueue(object : Callback<StudentSolo> {
            override fun onResponse(call: Call<StudentSolo>, response: Response<StudentSolo>) {
                if (response.isSuccessful) {
                    val student = response.body()
                    // Use the student data here, e.g., display it in the UI
                    student?.let {
                        println("Student Name: ${it.student_id}")
                        getStudent(it.student_id.toString())
                    }
                } else {
                    // Handle unsuccessful response
                    println("Failed to fetch data: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<StudentSolo>, t: Throwable) {
                // Handle failure (network issue, etc.)
                println("Error: ${t.message}")
            }
        })
    }
    private fun getStudent(studentId: String) {
        val studentService = RetrofitInstance.create(StudentService::class.java)
        studentService.getStudent(studentId).enqueue(object : Callback<StudentSolo> { // Change to Student (single object)
            override fun onResponse(
                call: Call<StudentSolo>,
                response: Response<StudentSolo>
            ) {
                if (response.isSuccessful) {
                    val student = response.body() // Single student object
                    if (student != null) {
                        setToViews(student)
                    }
                } else {
                    Log.e("Error", "Failed to fetch student details: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<StudentSolo>, t: Throwable) {
                Log.e("Error", "Failed to fetch student details: ${t.message}")
            }
        })
    }
    private fun setToViews(student: StudentSolo) {
        // Personal Info
        binding.firstName.setText(student.personal_info?.first_name ?: "N/A")
        binding.lastName.setText(student.personal_info?.last_name ?: "N/A")
        binding.middleName.setText(student.personal_info?.middle_name ?: "N/A")
        binding.sexDropdown.setText(student.personal_info?.gender ?: "N/A")
        binding.dateOfBirth.setText(student.personal_info?.birthdate ?: "N/A")

        // Contact Info
        val phoneContact = student.contacts?.find { it.contact_type == "Phone" }?.contact_value
        binding.phone.setText(phoneContact ?: "N/A")

        // Address Info
        val homeAddress = student.addresses?.find { it.address_type == "Home" }
        binding.municipalityDropdown.setText(homeAddress?.address_line1 ?: "N/A")
        binding.barangayDropdown.setText(homeAddress?.address_line2 ?: "N/A")

        // General Info
        binding.email.setText(student.student_email ?: "N/A")
        binding.level.setText(student.level ?: "N/A")
        binding.curriculum.setText(student.curriculum?.curriculum_code ?: "N/A")
        binding.entryDate.setText(student.entry_date ?: "N/A")
        binding.entryPeriod.setText(student.entry_period_id?.toString() ?: "N/A")
        binding.lrn.setText(student.learner_ref_no?.toString() ?: "N/A")
    }

}