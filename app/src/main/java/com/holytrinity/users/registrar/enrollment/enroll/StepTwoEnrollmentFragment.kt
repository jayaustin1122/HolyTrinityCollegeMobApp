package com.holytrinity.users.registrar.enrollment.enroll

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.api.SubjectsService
import com.holytrinity.databinding.FragmentStepTwoEnrollmentBinding
import com.holytrinity.model.SubjectsModel
import com.holytrinity.users.registrar.adapter.SubjectsAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class StepTwoEnrollmentFragment : Fragment() {
    private lateinit var binding : FragmentStepTwoEnrollmentBinding
    private lateinit var viewModel: ViewModelEnrollment
    private lateinit var subjectsAdapter: SubjectsAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStepTwoEnrollmentBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[ViewModelEnrollment::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get the level and add the correct suffix
        val level = viewModel.level.toString()
        val studentID = viewModel.studentID.toString()
        val yearLevel = when (level) {
            1.toString() -> "${level}st Year"
            2.toString() -> "${level}nd Year"
            3.toString() -> "${level}rd Year"
            4.toString() -> "${level}th Year"
            else -> "${level}th Year"
        }

        // Log the level
        Log.d("API_RESPONSE", "$yearLevel")

        // Get the curriculum ID
        val curriculum = viewModel.curr_id

        // Call the function to get all subjects
        getAllSubjects(yearLevel, curriculum.toString(), studentID.toString())
    }


    private fun getAllSubjects(yearLevel: String, curriculum: String, studentID: String) {
        // Create the Retrofit instance
        val service = RetrofitInstance.create(SubjectsService::class.java)


        service.getAllSubjectsCurriculum(yearLevel,curriculum,studentID).enqueue(object : Callback<List<SubjectsModel>> {
            override fun onResponse(call: Call<List<SubjectsModel>>, response: Response<List<SubjectsModel>>) {
                if (response.isSuccessful) {
                    // Access the subjects list directly from the response body
                    val subjects = response.body() ?: emptyList()
                    setupRecyclerView(subjects)
                    Log.d("API_RESPONSE", "Subjects received: ${subjects.size}")

                } else {
                    Log.e("API_ERROR", "Error fetching subjects: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<SubjectsModel>>, t: Throwable) {
                Log.e("API_ERROR", "Failed to connect", t)
            }
        })

    }


    private fun setupRecyclerView(subjects: List<SubjectsModel>) {
        subjectsAdapter = SubjectsAdapter(subjects)
        binding.subjectsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.subjectsRecyclerView.adapter = subjectsAdapter
    }
}