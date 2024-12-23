// InstructorClassListFragment.kt
package com.holytrinity.users.instructor.classlist

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.holytrinity.R
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.databinding.FragmentInstructorClassListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.navigation.fragment.findNavController
import com.holytrinity.api.ClassService
import com.holytrinity.model.ClassesResponse
import com.holytrinity.users.instructor.adapter.ClassesAdapter
import com.holytrinity.util.UserPreferences

class InstructorClassListFragment : Fragment(R.layout.fragment_instructor_class_list) {

    private lateinit var binding: FragmentInstructorClassListBinding
    private lateinit var adapter: ClassesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize View Binding
        binding = FragmentInstructorClassListBinding.bind(view)

        // Set up the back button
        binding.toolbarBackButton.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("selectedFragmentId", R.id.nav_dashboard_instructor)
            }
            findNavController().navigate(R.id.instructorDrawerHolderFragment, bundle)
        }

        adapter = ClassesAdapter(emptyList()) { selectedClassId ->
            // Handle item click using Bundle
            val bundle = Bundle().apply {
                putInt("class_id", selectedClassId)
            }
            findNavController().navigate(R.id.instructorClassDetailsFragment, bundle)
        }

        binding.eventRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.eventRecyclerView.adapter = adapter

        // Retrieve the user_id from SharedPreferences
        val userId = UserPreferences.getUserId(requireContext())

        if (userId == -1) {
            Toast.makeText(requireContext(), "User ID not found", Toast.LENGTH_SHORT).show()
            return
        }

        // Make the API call to fetch classes
        val apiService = RetrofitInstance.create(ClassService::class.java)
        apiService.getInstructorClasses(userId).enqueue(object : Callback<ClassesResponse> {
            override fun onResponse(call: Call<ClassesResponse>, response: Response<ClassesResponse>) {
                if (response.isSuccessful && response.body()?.success == true) {
                    val classes = response.body()?.classes ?: emptyList()
                    adapter.updateData(classes)
                } else {
                    Toast.makeText(requireContext(), "Failed to retrieve classes", Toast.LENGTH_SHORT).show()
                    Log.e("API_ERROR", "Response unsuccessful or success false")
                }
            }

            override fun onFailure(call: Call<ClassesResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("API_ERROR", t.message ?: "Unknown error")
            }
        })
    }
}
