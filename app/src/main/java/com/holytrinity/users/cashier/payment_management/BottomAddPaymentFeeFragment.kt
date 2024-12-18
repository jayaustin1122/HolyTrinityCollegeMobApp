package com.holytrinity.users.cashier.payment_management

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
import com.holytrinity.api.PaymentFeeService
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.databinding.FragmentBottomAddPaymentFeeBinding
import com.holytrinity.model.PaymentFee
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BottomSheetAddPaymentFeeFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomAddPaymentFeeBinding? = null
    private val binding get() = _binding!!
    private val apiService: PaymentFeeService by lazy {
        RetrofitInstance.create(PaymentFeeService::class.java)
    }

    // Callback to refresh data on dismissal
    var onDismissListener: (() -> Unit)? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomAddPaymentFeeBinding.inflate(inflater, container, false)
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
        val amountText = binding.amountEditText.text.toString().trim()
        val description = binding.descriptionEditText.text.toString().trim()

        if (title.isEmpty() || amountText.isEmpty() || description.isEmpty()) {
            Toast.makeText(context, "Please fill all fields.", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountText.toDoubleOrNull()
        if (amount == null) {
            Toast.makeText(context, "Invalid input for Amount", Toast.LENGTH_SHORT).show()
            return
        }

        SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
            .setTitleText("Add Payment Fee")
            .setContentText("Click \"Done\" to add this new payment fee.")
            .setConfirmText("Done")
            .setCancelText("Cancel")
            .setConfirmClickListener { dialog ->
                dialog.dismissWithAnimation()
                addPaymentFee(title, amount, description)
            }
            .setCancelClickListener { dialog ->
                dialog.dismissWithAnimation()
                Log.d("addPaymentFragment", "Add Cancelled")
            }
            .show()
    }

    private fun addPaymentFee(title: String, amount: Double, description: String) {
        val payment = PaymentFee(title, amount, description)

        apiService.addPaymentFee(payment).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    DialogUtils.showSuccessMessage(
                        requireActivity(),
                        "Success",
                        "Payment Fee Added Successfully"
                    ).show()
                    dismiss()
                } else {
                    Toast.makeText(context, "Failed to insert data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("addPaymentFee", "Error: ${t.message}")
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
