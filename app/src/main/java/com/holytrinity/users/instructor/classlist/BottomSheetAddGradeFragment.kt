package com.holytrinity.users.instructor.classlist

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.holytrinity.api.ClassService
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.databinding.FragmentBottomSheetAddGradeBinding
import com.holytrinity.model.StudentItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class BottomSheetAddGradeFragment : BottomSheetDialogFragment() {
    private lateinit var binding : FragmentBottomSheetAddGradeBinding
    private var studentId: Int = -1
    private var classId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomSheetAddGradeBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            studentId = it.getInt("student_id", -1)
            classId = it.getInt("class_id", -1)
        }
        getUserClass(studentId,classId)
        binding.btnSave.setOnClickListener {
            validateInputs(studentId, classId)
        }
    }

    private fun validateInputs(student_id: Int, class_id: Int) {
        val grade = binding.etGrade.text.toString().trim()
        val remarks = binding.etRemarks.text.toString().trim()

        if (grade.isNotEmpty()|| remarks.isNotEmpty()){
            updateGrade(student_id,class_id,grade.toInt(),remarks)
        }
        else{
            Toast.makeText(requireContext(), "All Fields Required.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateGrade(studentId: Int, classId: Int, grade: Int, remarks: String, attemptNum: Int = 1, status: String = "active") {
        val apiService = RetrofitInstance.create(ClassService::class.java)
        apiService.updateGradeStudent(studentId, classId, grade, remarks, attemptNum, status).enqueue(object : Callback<StudentItem> {
            override fun onResponse(call: Call<StudentItem>, response: Response<StudentItem>) {
                if (response.isSuccessful) {
                    val studentItem = response.body()
                    Toast.makeText(requireContext(), "Grade updated successfully", Toast.LENGTH_SHORT).show()
                    dismiss()

                } else {
                    Toast.makeText(requireContext(), "Failed to update grade", Toast.LENGTH_SHORT).show()
                    Log.e("API_ERROR", "Response unsuccessful or success false")
                }
            }

            override fun onFailure(call: Call<StudentItem>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("API_ERROR", t.message ?: "Unknown error")
            }
        })
    }


    private fun getUserClass(userId: Int, classId: Int) {
        val apiService = RetrofitInstance.create(ClassService::class.java)
        apiService.getStudentInClass(userId, classId).enqueue(object : Callback<StudentItem> {
            override fun onResponse(call: Call<StudentItem>, response: Response<StudentItem>) {
                if (response.isSuccessful) {
                    // Check if the student data exists in the response
                    val studentItem = response.body()

                    if (studentItem != null) {
                        // Extract grade and remark from the response body
                        val grade = studentItem.grade ?: "N/A"  // Default to "N/A" if grade is null
                        val remark = studentItem.remark ?: "No remarks"  // Default to "No remarks" if remark is null

                        binding.etGrade.setText(grade)
                        binding.etRemarks.setText(remark)
                    } else {
                        Toast.makeText(requireContext(), "Student data not found", Toast.LENGTH_SHORT).show()
                        Log.e("API_ERROR", "No student data found in the response")
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to retrieve students", Toast.LENGTH_SHORT).show()
                    Log.e("API_ERROR", "Response unsuccessful or success false")
                }
            }

            override fun onFailure(call: Call<StudentItem>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("API_ERROR", t.message ?: "Unknown error")
            }
        })
    }

}