package com.holytrinity.users.admin.scheduling

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.holytrinity.R
import com.holytrinity.api.ClassesService
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.api.sample.calendar.BottomSheetAddEventFragment
import com.holytrinity.databinding.FragmentAdminSchedulingBinding
import com.holytrinity.model.ClassResponses
import com.holytrinity.model.ClassesResponse
import com.holytrinity.users.admin.scheduling.BottomSheetAddScheduleFragment.Companion.TAG
import com.holytrinity.users.instructor.adapter.ClassesAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminSchedulingFragment : Fragment() {

    private lateinit var binding: FragmentAdminSchedulingBinding
    private lateinit var classesAdapter: ClassesAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminSchedulingBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbarBackButton.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("selectedFragmentId", null ?: R.id.nav_dashboard_admin)
            }
            findNavController().navigate(R.id.adminDrawerFragment, bundle)
        }
        binding.fabAddEvent.setOnClickListener {
            BottomSheetAddScheduleFragment().show(childFragmentManager, TAG)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        fetchClasses()
    }
    private fun fetchClasses() {
        val service = RetrofitInstance.create(ClassesService::class.java)
        service.getAllClasses().enqueue(object : Callback<ClassResponses> {
            override fun onResponse(call: Call<ClassResponses>, response: Response<ClassResponses>) {
                if (response.isSuccessful) {
                    val classes = response.body()?.classes ?: emptyList()
                    classesAdapter = ClassesAdapter(
                        classes,
                        onItemClick = { classId ->
                            })
                    binding.recyclerView.adapter = classesAdapter
                } else {
                    Log.e("API_ERROR", "Error fetching classes: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<ClassResponses>, t: Throwable) {
                Log.e("API_ERROR", "Failed to fetch classes", t)
            }
        })
    }

}