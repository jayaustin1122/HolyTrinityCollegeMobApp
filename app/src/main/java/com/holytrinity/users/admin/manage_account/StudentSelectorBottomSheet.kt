package com.holytrinity.users.admin.manage_account

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
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
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStudentSelectorBottomSheetBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userId = arguments?.getInt("user_id")
        getAllStudents()
    }
    private fun getAllStudents() {
        val studentService = RetrofitInstance.create(StudentService::class.java)
        studentService.getStudents().enqueue(object : Callback<List<Student>> {
            override fun onResponse(call: Call<List<Student>>, response: retrofit2.Response<List<Student>>) {
                if (response.isSuccessful) {
                    students = response.body() ?: emptyList()
                    students.forEach { student ->
                        Log.d("StudentData", "ID: ${student.student_id}, Name: ${student.student_name}")
                    }
                    studentNamesMap = students.associate { it.student_name.toString() to it.student_id.toString() }.toMutableMap()
                    setupAutoCompleteTextView()
                } else {
                    Log.e("Error", "Failed to fetch students: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Student>>, t: Throwable) {
                Log.e("Error", "Failed to fetch students: ${t.message}")
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
            val selectedStudent = students.find { it.student_id.toString() == selectedStudentID }

            if (selectedStudent != null) {
                binding.studentInfoLayout.visibility = View.VISIBLE
                binding.studentNumber.text = selectedStudent.student_id.toString()
                binding.studentName.text = selectedStudent.student_name
            }
        }
    }
}