package com.holytrinity.users.registrar.studentledger

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.holytrinity.R
import com.holytrinity.api.DepartmentService
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.databinding.FragmentBottomSheetFilterSoaBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BottomSheetFilterSoaFragment(private val onFilterApplied: (String) -> Unit) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentBottomSheetFilterSoaBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomSheetFilterSoaBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchDepartments()

        // Handle Done button click
        binding.doneButton.setOnClickListener {
            val selectedDept = binding.filterByDept.selectedItem.toString()

            // Pass the selected department back to the parent fragment
            onFilterApplied(selectedDept)
            dismiss() // Close the BottomSheet
        }
    }

    private fun fetchDepartments() {
        val service = RetrofitInstance.create(DepartmentService::class.java)
        service.getDepartments().enqueue(object : Callback<List<String>> {
            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                if (response.isSuccessful) {
                    val departments = response.body() ?: listOf()
                    val departmentsWithAll = listOf("All") + departments // Add "All" option
                    setupDepartmentSpinner(departmentsWithAll)
                }
            }

            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                Log.e("Error", "Failed to fetch departments: ${t.message}")
            }
        })
    }

    private fun setupDepartmentSpinner(departments: List<String>) {
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, departments)
        binding.filterByDept.adapter = adapter
    }
}
