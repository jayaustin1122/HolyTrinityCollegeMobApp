package com.holytrinity.users.admin.manage_account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.holytrinity.databinding.FilterManageAccountsBinding
import android.view.View
import com.holytrinity.R

class RoleFilterBottomSheet(private val onRoleSelected: (Int) -> Unit) : BottomSheetDialogFragment() {

    private lateinit var binding: FilterManageAccountsBinding
    private val roles = listOf(
        "All Roles", "Administrator", "Accounting", "Assistant", "Benefactor",
        "Cashier", "Counselor", "Instructor", "Parent", "Registrar", "Student"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FilterManageAccountsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val roleAdapter = ArrayAdapter(requireContext(), com.karumi.dexter.R.layout.support_simple_spinner_dropdown_item, roles)
        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.departmentSpinner.adapter = roleAdapter

        binding.doneButton.setOnClickListener {
            val selectedRole = binding.departmentSpinner.selectedItemPosition
            val roleId = when (selectedRole) {
                0 -> -1 // "All Roles"
                1 -> 1  // Administrator
                2 -> 3  // Accounting
                3 -> 8  // Assistant
                4 -> 10 // Benefactor
                5 -> 4  // Cashiering
                6 -> 9  // Counselor
                7 -> 5  // Instructor
                8 -> 6  // Parent
                9 -> 2  // Registrar
                10 -> 7 // Student
                else -> -1
            }
            onRoleSelected(roleId)
            dismiss()
        }
    }
}

