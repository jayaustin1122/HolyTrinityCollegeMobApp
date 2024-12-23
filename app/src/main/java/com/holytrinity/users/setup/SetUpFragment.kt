package com.holytrinity.users.setup

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.holytrinity.R
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.api.SetUpService
import com.holytrinity.api.SoaService
import com.holytrinity.databinding.FragmentSetUpBinding
import com.holytrinity.model.Enrollment_Period

import com.holytrinity.util.SharedPrefsUtil
import com.holytrinity.util.UserPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SetUpFragment : Fragment() {
    private lateinit var binding: FragmentSetUpBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSetUpBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val savedPeriod = SharedPrefsUtil.getSelectedPeriod(requireContext())

        if (savedPeriod != null) {
            Log.d(
                "SavedPeriod", "Year: ${savedPeriod.enrollment_period_id}, Semester: ${savedPeriod.semester}, " +
                        "Start Date: ${savedPeriod.start_date}, End Date: ${savedPeriod.end_date}"
            )
        } else {
            Log.d("SavedPeriod", "No saved period found.")
        }

        val roleId = UserPreferences.getRoleId(requireContext())

        binding.toolbarBackButton.setOnClickListener {
            navigateBasedOnRole(roleId)
        }

        fetchEnrollmentPeriods()
    }

    private fun navigateBasedOnRole(roleId: Int) {
        when (roleId) {
            1 -> findNavController().navigate(R.id.adminDrawerFragment)
            2 -> findNavController().navigate(R.id.registrarDrawerHolderFragment)
            4 -> findNavController().navigate(R.id.cashierDrawerFragment)
            5 -> findNavController().navigate(R.id.instructorDrawerHolderFragment)
            6 -> findNavController().navigate(R.id.parentDrawerHolderFragment)
            7 -> findNavController().navigate(R.id.studentDrawerHolderFragment)
            10 -> findNavController().navigate(R.id.benefactorDrawerHolderFragment)
            else -> {
                // Default navigation if roleId doesn't match any of the above
                Toast.makeText(
                    requireContext(),
                    "Invalid role, navigating back to dashboard",
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().navigate(R.id.nav_dashboard) // You can replace this with a default fragment
            }
        }
    }

    private fun fetchEnrollmentPeriods() {
        val apiService = RetrofitInstance.create(SetUpService::class.java)
        apiService.getEnrollmentPeriods().enqueue(object : Callback<List<Enrollment_Period>> {
            override fun onResponse(
                call: Call<List<Enrollment_Period>>,
                response: Response<List<Enrollment_Period>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    populateSpinner(response.body()!!)
                } else {
                    Toast.makeText(requireContext(), "Failed to fetch periods", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onFailure(call: Call<List<Enrollment_Period>>, t: Throwable) {
                Log.e("SetUpFragment", "Error fetching periods", t)
                Toast.makeText(requireContext(), "Error fetching periods", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun populateSpinner(periods: List<Enrollment_Period>) {
        val studentNamesList =
            periods.map { "${it.year} ${it.semester} (${it.start_date} - ${it.end_date})" }
        val autoCompleteTextView: AutoCompleteTextView =
            binding.root.findViewById(R.id.semesterAutoCompleteTextView)
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            studentNamesList
        )
        autoCompleteTextView.setAdapter(adapter)

        autoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            val selectedPeriod = periods[position]

            SharedPrefsUtil.saveSelectedPeriod(requireContext(), selectedPeriod)

            Toast.makeText(
                requireContext(),
                "Selected: ${selectedPeriod.semester}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}