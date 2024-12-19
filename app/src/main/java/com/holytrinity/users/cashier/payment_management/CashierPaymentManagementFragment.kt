package com.holytrinity.users.cashier.payment_management

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.canorecoapp.utils.DialogUtils
import com.holytrinity.R
import com.holytrinity.api.PaymentFeeService
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.databinding.FragmentCashierPaymentManagementBinding
import com.holytrinity.api.ApiResponse
import com.holytrinity.api.PaymentFee
import com.holytrinity.users.cashier.adapter.PaymentFeeAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

open class CashierPaymentManagementFragment : Fragment() {

    private lateinit var loadingDialog: SweetAlertDialog
    private lateinit var binding: FragmentCashierPaymentManagementBinding
    private val apiService: PaymentFeeService by lazy {
        RetrofitInstance.create(PaymentFeeService::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCashierPaymentManagementBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Show Loading Dialog
        loadingDialog = DialogUtils.showLoading(requireActivity())
        loadingDialog.show()

        // Initialize RecyclerView
        binding.paymentFeeRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Fetch data from the server
        fetchPayments()

        binding.toolbarBackButton.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("selectedFragmentId", null ?: R.id.nav_dashboard_cashier)
            }
            findNavController().navigate(R.id.cashierDrawerFragment, bundle)
        }

        binding.addPaymentButton.setOnClickListener {
            val detailsDialog = BottomSheetAddPaymentFeeFragment().apply {
                onDismissListener = {
                    fetchPayments()
                }
            }
            detailsDialog.show(childFragmentManager, "BottomSheetAddPaymentFragment")
        }
    }

    private fun fetchPayments() {
        apiService.getPaymentFee("getPaymentFee").enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val payments = response.body()?.payment ?: emptyList()

                    // Check if data exists before setting the adapter
                    if (payments.isNotEmpty()) {
                        val adapter = PaymentFeeAdapter(payments.toMutableList()) { payment ->
                            // On delete button click, show confirmation
                            showDeleteConfirmation(payment)
                        }
                        binding.paymentFeeRecyclerView.adapter = adapter
                    } else {
                        Toast.makeText(context, "No payments available", Toast.LENGTH_SHORT).show()
                    }

                    loadingDialog.dismiss()
                } else {
                    Toast.makeText(context, "Failed to fetch payments", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                loadingDialog.dismiss()
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("CashierPaymentManagement", "Error: ${t.message}")
            }
        })
    }

    private fun showDeleteConfirmation(payment: PaymentFee) {
        SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
            .setTitleText("Delete Payment")
            .setContentText("Are you sure you want to delete this payment?")
            .setConfirmText("Yes")
            .setCancelText("No")
            .setConfirmClickListener { dialog ->
                dialog.dismissWithAnimation()
                deletePayment(payment)
            }
            .setCancelClickListener { dialog ->
                dialog.dismissWithAnimation()
            }
            .show()
    }

    private fun deletePayment(payment: PaymentFee) {
        val requestBody = mapOf("id" to payment.id)

        apiService.deletePaymentFee(requestBody).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    Toast.makeText(context, "Payment deleted successfully", Toast.LENGTH_SHORT).show()
                    // Update the UI after successful deletion
                    (binding.paymentFeeRecyclerView.adapter as PaymentFeeAdapter).deleteItem(payment)
                } else {
                    Toast.makeText(context, "Failed to delete payment", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("CashierPaymentManagement", "Error: ${t.message}")
            }
        })
    }
}
