package com.holytrinity.users.student.admit.steps

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.datepicker.MaterialDatePicker
import com.holytrinity.R
import com.holytrinity.databinding.FragmentStudentGetAdmittedStepOneBinding
import com.holytrinity.util.Municipalities.municipalitiesWithBarangays
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class StudentGetAdmittedStepOneFragment : Fragment() {
    private lateinit var binding : FragmentStudentGetAdmittedStepOneBinding
    private lateinit var viewModel: ViewModelAdmit
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStudentGetAdmittedStepOneBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(ViewModelAdmit::class.java)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.firstName.addTextChangedListener {
            viewModel.firstName = it.toString()
        }
        binding.lastName.addTextChangedListener {
            viewModel.lastName = it.toString()
        }
        binding.middleName.addTextChangedListener {
            viewModel.middleName = it.toString()
        }
        val sexItems = listOf("Male", "Female", "Other")
        val sexAdapter = ArrayAdapter(requireContext(), R.layout.address_item_views, sexItems)
        binding.sexDropdown.setAdapter(sexAdapter)
        binding.sexDropdown.addTextChangedListener {
            viewModel.sex = it.toString()
        }
        binding.sexDropdown.setText(sexItems[0], false)
        binding.dateOfBirth.setOnClickListener {
            showDatePickerDialog()
        }
        binding.email.addTextChangedListener {
            viewModel.email = it.toString()
        }
        binding.phone.addTextChangedListener {
            viewModel.phone = it.toString()
        }
        val municipalities = municipalitiesWithBarangays.keys.toList()
        val municipalityAdapter = ArrayAdapter(requireContext(), R.layout.address_item_views, municipalities)
        binding.municipalityDropdown.setAdapter(municipalityAdapter)

        binding.municipalityDropdown.setOnItemClickListener { parent, view, position, id ->
            val selectedMunicipality = parent.getItemAtPosition(position).toString()
            viewModel.municipality = selectedMunicipality

            val barangays = municipalitiesWithBarangays[selectedMunicipality] ?: emptyList()
            val barangayAdapter = ArrayAdapter(requireContext(), R.layout.address_item_views, barangays)
            binding.barangayDropdown.setAdapter(barangayAdapter)
        }

        binding.barangayDropdown.setOnItemClickListener { parent, view, position, id ->
            val selectedBarangay = parent.getItemAtPosition(position).toString()
            viewModel.barangay = selectedBarangay
        }


    }
    private fun showDatePickerDialog() {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Date")
            .build()
        datePicker.addOnPositiveButtonClickListener { selection ->
            val calendar = Calendar.getInstance().apply {
                timeInMillis = selection
            }
            val formattedDate = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(calendar.time)
            viewModel.dateOfBirth = formattedDate
            binding.dateOfBirth.setText(formattedDate)
        }
        datePicker.show(parentFragmentManager, "MaterialDatePicker")
    }

}