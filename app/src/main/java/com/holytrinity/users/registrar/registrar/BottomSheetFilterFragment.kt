package com.holytrinity.users.registrar.registrar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.holytrinity.R
import com.holytrinity.databinding.FragmentBottomSheetFilterBinding

class BottomSheetFilterFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentBottomSheetFilterBinding
    private var filterListener: ((String?) -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomSheetFilterBinding.inflate(inflater, container, false)

        // Set up spinner options for year
        val yearOptions = arrayOf("1st Year", "2nd Year", "3rd Year", "4th Year")
        val yearAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, yearOptions)
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.yearSpinner.adapter = yearAdapter

        // Handle the Done button click to apply the filter
        binding.doneButton.setOnClickListener {
            val selectedYear = binding.yearSpinner.selectedItem as String?
            filterListener?.invoke(selectedYear)
            dismiss()
        }

        return binding.root
    }

    fun setFilterListener(listener: (String?) -> Unit) {
        this.filterListener = listener
    }
}
