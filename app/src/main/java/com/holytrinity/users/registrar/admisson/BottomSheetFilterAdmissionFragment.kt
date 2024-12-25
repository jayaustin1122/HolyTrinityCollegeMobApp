package com.holytrinity.users.registrar.admisson

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.holytrinity.R
import com.holytrinity.databinding.FragmentBottomSheetFilterAdmissionBinding

class BottomSheetFilterAdmissionFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentBottomSheetFilterAdmissionBinding

    // Callback to pass the chosen filter back to parent
    var onFilterApplied: ((String) -> Unit)? = null

    // We define 3 possible filter states:
    private val filterOptions = listOf(
        "Pending",
        "Admitted",
        "Denied"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBottomSheetFilterAdmissionBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1) Populate the spinner
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, filterOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.filterByDept.adapter = adapter

        // 2) Handle done/apply button
        binding.doneButton.setOnClickListener {
            // Get the selected spinner value
            val selectedPosition = binding.filterByDept.selectedItemPosition
            // Convert it to "0" / "1" / "2"
            val regVerified = when (selectedPosition) {
                0 -> "0" // Pending
                1 -> "1" // Admitted
                2 -> "2" // Denied
                else -> null
            }

            // Call the callback if not null
            regVerified?.let {
                onFilterApplied?.invoke(it)
            }
            dismiss()
        }
    }
}
