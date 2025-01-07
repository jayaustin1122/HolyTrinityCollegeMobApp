package com.holytrinity.users.admin.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.holytrinity.R
import com.holytrinity.api.DashboardService
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.databinding.FragmentAdminDashboardBinding
import com.holytrinity.model.DashboardStats
import com.holytrinity.util.UserPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminDashboardFragment : Fragment() {

    private lateinit var binding: FragmentAdminDashboardBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdminDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ipakita ang name ng user (Halimbawa: "Alice B Smith")
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
                    binding.tvTotalStudents.text     = stats.total_students.toString()
                    binding.tvAdmittedStudents.text  = stats.admitted_students.toString()
                    binding.tvPendingStudents.text   = stats.pending_students.toString()
                    binding.tvDeniedStudents.text    = stats.denied_students.toString()
                    binding.tvEnrolledStudents.text  = stats.enrolled_students.toString()
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
