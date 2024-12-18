package com.holytrinity.users.registrar.fee_management

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.canorecoapp.utils.DialogUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.holytrinity.api.ApiResponse
import com.holytrinity.api.DiscountFeeService
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.databinding.FragmentBottomSheetAddDiscountBinding
import com.holytrinity.model.DiscountFee
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BottomSheetAddDiscountFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomSheetAddDiscountBinding? = null
    private val binding get() = _binding!!
    private val apiService: DiscountFeeService by lazy {
        RetrofitInstance.create(DiscountFeeService::class.java)
    }

    // Callback to refresh data on dismissal
    var onDismissListener: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomSheetAddDiscountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.doneButton.setOnClickListener {
            validateAndConfirmData()
        }
    }

    private fun validateAndConfirmData() {
        val title = binding.titleEditText.text.toString().trim()
        val code = binding.codeEditText.text.toString().trim()
        val amountText = binding.amountEditText.text.toString().trim()
        val description = binding.descriptionEditText.text.toString().trim()

        if (title.isEmpty() || code.isEmpty() || amountText.isEmpty() || description.isEmpty()) {
            Toast.makeText(context, "Please fill all fields.", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountText.toDoubleOrNull()
        if (amount == null) {
            Toast.makeText(context, "Invalid input for Amount", Toast.LENGTH_SHORT).show()
            return
        }

        SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
            .setTitleText("Add Discount Fee")
            .setContentText("Click \"Done\" to add this new discount fee.")
            .setConfirmText("Done")
            .setCancelText("Cancel")
            .setConfirmClickListener { dialog ->
                dialog.dismissWithAnimation()
                addDiscountFee(title, code, amount, description)
            }
            .setCancelClickListener { dialog ->
                dialog.dismissWithAnimation()
                Log.d("addDiscountFragment", "Add Cancelled")
            }
            .show()
    }

    private fun addDiscountFee(title: String, code: String, amount: Double, description: String) {
        val discountFee = DiscountFee(title, code, amount, description)

        apiService.addDiscountFee(discountFee).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    DialogUtils.showSuccessMessage(
                        requireActivity(),
                        "Success",
                        "Discount Fee Added Successfully"
                    ).show()
                    dismiss()
                } else {
                    Toast.makeText(context, "Failed to insert data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("addDiscountFee", "Error: ${t.message}")
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
