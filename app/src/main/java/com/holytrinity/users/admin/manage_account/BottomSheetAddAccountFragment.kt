package com.holytrinity.users.admin.manage_account

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.holytrinity.api.AccountRequest
import com.holytrinity.api.ApiResponse
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.api.AccountService
import com.holytrinity.databinding.FragmentBottomSheetAddAccountBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BottomSheetAddAccountFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomSheetAddAccountBinding? = null
    private val binding get() = _binding!!
    private val apiService: AccountService by lazy {
        RetrofitInstance.create(AccountService::class.java)
    }

    // Callback to refresh data on dismissal
    var onDismissListener: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBottomSheetAddAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Populate spinner with roles
        val roles = mapOf(
            1 to "Administrator",
            3 to "Accounting",
            8 to "Assistant",
            10 to "Benefactor",
            4 to "Cashiering",
            9 to "Counselor",
            5 to "Instructor",
            6 to "Parent",
            2 to "Registrar",
            7 to "Student"
        )
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            roles.values.toList()
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.roleSpinner.adapter = adapter

        binding.doneButton.setOnClickListener {
            validateAndConfirmData(roles)
        }
    }

    private fun validateAndConfirmData(roles: Map<Int, String>) {
        val roleId = roles.keys.elementAt(binding.roleSpinner.selectedItemPosition)
        val username = binding.usernameEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()
        val name = binding.nameEditText.text.toString().trim()

        if (username.isEmpty() || password.isEmpty() || name.isEmpty()) {
            Toast.makeText(context, "Please fill all fields.", Toast.LENGTH_SHORT).show()
            return
        }

        SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
            .setTitleText("Add Account")
            .setContentText("Click \"Done\" to add this new account.")
            .setConfirmText("Done")
            .setCancelText("Cancel")
            .setConfirmClickListener { dialog ->
                dialog.dismissWithAnimation()
                addAccount(roleId, username, password, name)
            }
            .setCancelClickListener { dialog ->
                dialog.dismissWithAnimation()
            }
            .show()
    }

    private fun addAccount(roleId: Int, username: String, password: String, name: String) {
        val accountRequest = AccountRequest(
            role_id = roleId,
            username = username,
            password_hash = password,
            name = name
        )

        apiService.addAccount(accountRequest).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    Toast.makeText(context, "Account added successfully!", Toast.LENGTH_SHORT).show()
                    dismiss()
                } else {
                    Toast.makeText(context, "Failed to add account.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener?.invoke() // Trigger the callback when dialog is dismissed
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}