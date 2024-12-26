package com.holytrinity.users.instructor.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.holytrinity.R
import com.holytrinity.api.DashboardService
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.databinding.FragmentInstructorDashboardBinding
import com.holytrinity.model.DashboardStats
import com.holytrinity.util.UserPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InstructorDashboardFragment : Fragment() {

    private lateinit var binding: FragmentInstructorDashboardBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =  FragmentInstructorDashboardBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.textViewUser.text = UserPreferences.getName(requireContext())

        // Tawagin ang function para kunin ang stats
        fetchDashboardStats()
    }

    private fun fetchDashboardStats() {
        val dashboardService = RetrofitInstance.create(DashboardService::class.java)
        dashboardService.getDashboardStats().enqueue(object : Callback<DashboardStats> {
            override fun onResponse(call: Call<DashboardStats>, response: Response<DashboardStats>) {
                if (response.isSuccessful && response.body() != null) {
                    val stats = response.body()!!
                    // Ipakita ang tatlong bilang
                    binding.tvTotalStudents.text     = stats.total_students.toString()
                    binding.tvAdmittedStudents.text  = stats.admitted_students.toString()
                    binding.tvPendingStudents.text   = stats.pending_students.toString()
                } else {
                    Toast.makeText(requireContext(),
                        "Failed to load dashboard stats",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<DashboardStats>, t: Throwable) {
                Toast.makeText(requireContext(),
                    "Error: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
}