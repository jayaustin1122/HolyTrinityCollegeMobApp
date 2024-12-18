package com.holytrinity.users.registrar.fee_management

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.canorecoapp.utils.DialogUtils
import com.holytrinity.api.DiscountFeeService
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.databinding.FragmentRegistrarFeeTab2Binding
import com.holytrinity.api.ApiResponse
import com.holytrinity.api.DiscountFee
import com.holytrinity.users.registrar.adapter.AssessmentFeeAdapter
import com.holytrinity.users.registrar.adapter.DiscountFeeAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

open class RegistrarFeeTab2Fragment : Fragment() {

    private lateinit var loadingDialog: SweetAlertDialog
    private lateinit var binding: FragmentRegistrarFeeTab2Binding
    private val apiService: DiscountFeeService by lazy {
        RetrofitInstance.create(DiscountFeeService::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrarFeeTab2Binding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Show Loading Dialog
        loadingDialog = DialogUtils.showLoading(requireActivity())
        loadingDialog.show()

        // Initialize RecyclerView
        binding.discountFeeRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Fetch data from the server
        fetchDiscountFees()

        binding.addFeeButton.setOnClickListener {
            val detailsDialog = BottomSheetAddDiscountFragment().apply {
                onDismissListener = {
                    fetchDiscountFees()
                }
            }
            detailsDialog.show(childFragmentManager, "BottomSheetAddDiscountFragment")
        }
    }

    private fun fetchDiscountFees() {
        apiService.getDiscountFees("getDiscountFees").enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val discountFees = response.body()?.discount ?: emptyList()

                    // Check if data exists before setting the adapter
                    if (discountFees.isNotEmpty()) {
                        val adapter = DiscountFeeAdapter(
                            discountFees.toMutableList(),
                            deleteListener = { fee ->
                                // On delete button click, show confirmation
                                showDeleteConfirmation(fee)
                            },
                            editListener = { fee ->
                                // On edit button click, open the bottom sheet in edit mode
                                val editDialog = BottomSheetAddDiscountFragment().apply {
                                    arguments = Bundle().apply {
                                        putInt("id", fee.id)
                                        putString("title", fee.title)
                                        putString("code", fee.code)
                                        putDouble("amount", fee.amount)
                                        putString("description", fee.description)
                                    }
                                    onDismissListener = {
                                        fetchDiscountFees()
                                    }
                                }
                                editDialog.show(childFragmentManager, "BottomSheetAddAssessmentFragment")
                            }
                        )
                        binding.discountFeeRecyclerView.adapter = adapter
                    } else {
                        Toast.makeText(context, "No discount fees available", Toast.LENGTH_SHORT).show()
                    }

                    loadingDialog.dismiss()
                } else {
                    loadingDialog.dismiss()
                    Toast.makeText(context, "Failed to fetch fees", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                loadingDialog.dismiss()
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("RegistrarFeeTab2", "Error: ${t.message}")
            }
        })
    }

    private fun showDeleteConfirmation(fee: DiscountFee) {
        SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
            .setTitleText("Delete Discount Fee")
            .setContentText("Are you sure you want to delete this discount fee?")
            .setConfirmText("Yes")
            .setCancelText("No")
            .setConfirmClickListener { dialog ->
                dialog.dismissWithAnimation()
                deleteDiscountFee(fee)
            }
            .setCancelClickListener { dialog ->
                dialog.dismissWithAnimation()
            }
            .show()
    }

    private fun deleteDiscountFee(fee: DiscountFee) {
        val requestBody = mapOf("id" to fee.id)

        apiService.deleteDiscountFee(requestBody).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    Toast.makeText(context, "Fee deleted successfully", Toast.LENGTH_SHORT).show()
                    // Update the UI after successful deletion
                    (binding.discountFeeRecyclerView.adapter as DiscountFeeAdapter).deleteItem(fee)
                } else {
                    Toast.makeText(context, "Failed to delete fee", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("RegistrarFeeTab2", "Error: ${t.message}")
            }
        })
    }
}
