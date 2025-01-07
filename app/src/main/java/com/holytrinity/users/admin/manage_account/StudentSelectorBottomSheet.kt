package com.holytrinity.users.admin.manage_account

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.holytrinity.R
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.api.StudentService
import com.holytrinity.databinding.FragmentStudentSelectorBottomSheetBinding
import com.holytrinity.model.Student
import retrofit2.Call
import retrofit2.Callback

class StudentSelectorBottomSheet : BottomSheetDialogFragment() {
    private lateinit var binding : FragmentStudentSelectorBottomSheetBinding
    private lateinit var students: List<Student>
    private lateinit var studentNamesMap: MutableMap<String, String>
    private lateinit var studentNames: MutableMap<String, String>
    private var userId: Int? = null
    private var from: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStudentSelectorBottomSheetBinding.inflate(layoutInflater)
        Log.d("StudentSelector", "Fragment view created.")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userId = arguments?.getInt("user_id")
        from = arguments?.getInt("role")

        Log.d("StudentSelector", "UserId: $userId, Role: $from")

        getAllStudents()
        binding.doneButton.setOnClickListener {
            Log.d("StudentSelector", "Done button clicked.")
            addStudent()
        }
    }

    private fun addStudent() {
        val studentIdString = binding.studentNumber.text.toString().trim()
        if (studentIdString.isEmpty()) {
            Log.d("StudentSelector", "Student ID is empty.")
            Toast.makeText(requireContext(), "Please search or select a student before uploading", Toast.LENGTH_SHORT).show()
            return
        }

        val studentId = studentIdString.toIntOrNull()
        if (studentId == null) {
            Log.d("StudentSelector", "Invalid student number: $studentIdString")
            Toast.makeText(requireContext(), "Invalid student number", Toast.LENGTH_SHORT).show()
            return
        }

        val fromRole = when (from) {
            1 -> "Administrator"
            3 -> "Accounting"
            8 -> "Assistant"
            10 -> "Benefactor"
            4 -> "Cashiering"
            9 -> "Counselor"
            5 -> "Instructor"
            6 -> "Parent"
            2 -> "Registrar"
            7 -> "Student"
            else -> "Unknown"
        }

        Log.d("StudentSelector", "Role: $fromRole, UserId: $userId, StudentId: $studentId")

        val studentService = RetrofitInstance.create(StudentService::class.java)
        val call = studentService.addStudentsBene( userId,studentId, fromRole, "")
        Log.d("StudentSelector", "Sending request with parameters: student_id=$studentId, user_id=$userId, from_role=$fromRole")
        call.enqueue(object : Callback<List<Student>> {
            override fun onResponse(call: Call<List<Student>>, response: retrofit2.Response<List<Student>>) {
                if (response.isSuccessful) {
                    Log.d("StudentSelector", "Student added successfully.")
                    Log.d("StudentSelector", "Response: ${response.body()}")
                    dismiss()
                    Toast.makeText(requireContext(), "Student Added", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("StudentSelector", "Failed to add student: ${response.code()}")
                    response.errorBody()?.let {
                        Log.e("StudentSelector", "Error Response: ${it.string()}")
                    }
                }
            }

            override fun onFailure(call: Call<List<Student>>, t: Throwable) {
                Log.e("StudentSelector", "Failed to add student: ${t.message}")
            }
        })

    }


    private fun getAllStudents() {
        Log.d("StudentSelector", "Fetching all students...")
        val studentService = RetrofitInstance.create(StudentService::class.java)
        studentService.getStudents().enqueue(object : Callback<List<Student>> {
            override fun onResponse(call: Call<List<Student>>, response: retrofit2.Response<List<Student>>) {
                if (response.isSuccessful) {
                    students = response.body() ?: emptyList()
                    Log.d("StudentSelector", "Fetched ${students.size} students.")

                    students.forEach { student ->
                        Log.d("StudentData", "ID: ${student.student_id}, Name: ${student.student_name}")
                    }

                    studentNamesMap = students.associate { it.student_name.toString() to it.student_id.toString() }.toMutableMap()
                    setupAutoCompleteTextView()
                } else {
                    Log.e("StudentSelector", "Failed to fetch students: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Student>>, t: Throwable) {
                Log.e("StudentSelector", "Failed to fetch students: ${t.message}")
            }
        })
    }

    private fun setupAutoCompleteTextView() {
        val studentNamesList = studentNamesMap.keys.toList()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, studentNamesList)
        binding.searchStudentTextView.setAdapter(adapter)

        binding.searchStudentTextView.setOnItemClickListener { parent, _, position, _ ->
            val selectedName = parent.getItemAtPosition(position) as String
            val selectedStudentID = studentNamesMap[selectedName]
            Log.d("StudentSelector", "Student selected: $selectedName with ID: $selectedStudentID")

            val selectedStudent = students.find { it.student_id.toString() == selectedStudentID }

            if (selectedStudent != null) {
                Log.d("StudentSelector", "Displaying student info for ${selectedStudent.student_name}")
                binding.studentInfoLayout.visibility = View.VISIBLE
                binding.studentNumber.text = selectedStudent.student_id.toString()
                binding.studentName.text = selectedStudent.student_name
            }
        }
    }
}
