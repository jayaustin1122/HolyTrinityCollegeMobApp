package com.holytrinity.users.parent.child

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.api.ParentChildWrapper
import com.holytrinity.api.StudentService
import com.holytrinity.databinding.FragmentParentChildBinding
import com.holytrinity.model.StudentSolo
import com.holytrinity.util.UserPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ParentChildFragment : Fragment() {
    private lateinit var binding: FragmentParentChildBinding
    private lateinit var studentAdapter: StudentAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentParentChildBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userId = UserPreferences.getUserId(requireContext())

        binding.textViewUser.text = UserPreferences.getName(requireContext())

        // Initialize the adapter once
        studentAdapter = StudentAdapter(mutableListOf())
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = studentAdapter

        // Fetch students for the parent
        getParentStudents(userId.toString())
    }

    private fun getParentStudents(userId: String) {
        val parentService = RetrofitInstance.create(StudentService::class.java)
        parentService.getParentChild(userId).enqueue(object : Callback<ParentChildWrapper> {
            override fun onResponse(
                call: Call<ParentChildWrapper>,
                response: Response<ParentChildWrapper>
            ) {
                if (response.isSuccessful) {
                    val students = response.body()?.students ?: emptyList()
                    if (students.isNotEmpty()) {
                        // Fetch individual student data and populate list
                        for (studentId in students) {
                            getStudent(studentId.toString())
                        }
                    } else {
                        Log.e("Error", "No students found for this parent.")
                    }
                } else {
                    Log.e("Error", "Failed to fetch parent-child details: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ParentChildWrapper>, t: Throwable) {
                Log.e("Error", "Failed to fetch parent-child details: ${t.message}")
            }
        })
    }

    private fun getStudent(studentId: String) {
        val studentService = RetrofitInstance.create(StudentService::class.java)
        studentService.getStudent(studentId).enqueue(object : Callback<StudentSolo> {
            override fun onResponse(
                call: Call<StudentSolo>,
                response: Response<StudentSolo>
            ) {
                if (response.isSuccessful) {
                    val student = response.body()
                    student?.let {
                        // Add student to the adapter and notify change
                        studentAdapter.students.add(it)
                        studentAdapter.notifyItemInserted(studentAdapter.students.size - 1)
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
}
