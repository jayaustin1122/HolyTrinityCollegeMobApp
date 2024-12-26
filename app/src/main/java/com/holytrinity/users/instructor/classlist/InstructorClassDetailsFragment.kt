// InstructorClassDetailsFragment.kt
package com.holytrinity.users.instructor.classlist

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.holytrinity.R
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.databinding.FragmentInstructorClassDetailsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.navigation.fragment.navArgs
import com.holytrinity.api.ClassService
import com.holytrinity.model.ClassResponses
import com.holytrinity.model.StudentsResponse
import com.holytrinity.users.instructor.adapter.StudentsAdapter

class InstructorClassDetailsFragment : Fragment(R.layout.fragment_instructor_class_details) {

    private lateinit var binding: FragmentInstructorClassDetailsBinding
    private lateinit var adapter: StudentsAdapter
    private var classId: Int = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize View Binding
        binding = FragmentInstructorClassDetailsBinding.bind(view)

        // Retrieve the class_id from arguments
        arguments?.let {
            classId = it.getInt("class_id", -1)
        }

        if (classId == -1) {
            Toast.makeText(requireContext(), "Invalid Class ID", Toast.LENGTH_SHORT).show()
            return
        }

        // Set up the back button
        binding.toolbarBackButton.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("selectedFragmentId", R.id.nav_dashboard_instructor)
            }
            findNavController().navigate(R.id.instructorDrawerHolderFragment, bundle)
        }

        // Initialize RecyclerView with an empty list
        adapter = StudentsAdapter(emptyList())
        binding.eventRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.eventRecyclerView.adapter = adapter

        // Optionally, display class details at the top
        // Fetch and display class details if needed using classId

        // Make the API call to fetch enrolled students
        val apiService = RetrofitInstance.create(ClassService::class.java)
        apiService.getEnrolledStudents(classId).enqueue(object : Callback<StudentsResponse> {
            override fun onResponse(call: Call<StudentsResponse>, response: Response<StudentsResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val students = response.body()?.students ?: emptyList()
                    adapter.updateData(students)
                } else {
                    Toast.makeText(requireContext(), "Failed to retrieve students", Toast.LENGTH_SHORT).show()
                    Log.e("API_ERROR", "Response unsuccessful or success false")
                }
            }

            override fun onFailure(call: Call<StudentsResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("API_ERROR", t.message ?: "Unknown error")
            }
        })
    }
}
