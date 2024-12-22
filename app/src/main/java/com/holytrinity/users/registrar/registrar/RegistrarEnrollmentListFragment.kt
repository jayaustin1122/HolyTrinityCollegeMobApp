package com.holytrinity.users.registrar.registrar

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.holytrinity.R
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.api.StudentService
import com.holytrinity.databinding.FragmentRegistratEnrollmentListBinding
import com.holytrinity.model.Student
import com.holytrinity.users.admin.adapter.EnrolledAdapter
import retrofit2.Call
import retrofit2.Callback

class RegistrarEnrollmentListFragment : Fragment() {
    private lateinit var binding: FragmentRegistratEnrollmentListBinding
    private var filteredStudents: List<Student> = emptyList()
    private lateinit var studentsAdapter: EnrolledAdapter
    private var students: List<Student> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegistratEnrollmentListBinding.inflate(layoutInflater)
        studentsAdapter = EnrolledAdapter(filteredStudents) { studentId ->
            // Handle item click
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerEnrolledSubjects.layoutManager = LinearLayoutManager(context)
        binding.recyclerEnrolledSubjects.adapter = studentsAdapter
        binding.searchStudentTextView.addTextChangedListener {
            val query = it.toString().trim()
            filterStudents(query)
        }
        getAllStudents()
    }

    private fun getAllStudents(studentId: String? = null, registrationVerified: String? = null) {
        val studentService = RetrofitInstance.create(StudentService::class.java)
        studentService.getStudents(studentId, registrationVerified).enqueue(object :
            Callback<List<Student>> {
            override fun onResponse(
                call: Call<List<Student>>,
                response: retrofit2.Response<List<Student>>
            ) {
                if (response.isSuccessful) {
                    students = response.body() ?: emptyList()
                    binding.countTitleTextView.text = "Results: ${filteredStudents.size+1}"
                    filteredStudents = students.filter { it.official_status == "Official" }
                    studentsAdapter.updateData(filteredStudents)
                } else {
                    Log.e("Error", "Failed to fetch students: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Student>>, t: Throwable) {
                Log.e("Error", "Failed to fetch students: ${t.message}")
            }
        })
    }

    private fun filterStudents(query: String) {
        filteredStudents = if (query.isEmpty()) {
            students.filter { it.official_status == "Official" }
        } else {
            students.filter {
                it.official_status == "Official" && it.student_name?.contains(query, ignoreCase = true) == true
            }
        }

        studentsAdapter.updateData(filteredStudents)
        binding.countTitleTextView.text = "Count: ${filteredStudents.size}"
    }

}
