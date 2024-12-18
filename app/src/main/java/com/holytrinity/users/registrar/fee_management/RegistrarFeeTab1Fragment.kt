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
import com.holytrinity.api.AssessmentFeeService
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.databinding.FragmentRegistrarFeeTab1Binding
import com.holytrinity.api.ApiResponse
import com.holytrinity.api.AssessmentFee
import com.holytrinity.users.registrar.adapter.AssessmentFeeAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

open class RegistrarFeeTab1Fragment : Fragment() {

    private lateinit var loadingDialog: SweetAlertDialog
    private lateinit var binding: FragmentRegistrarFeeTab1Binding
    private val apiService: AssessmentFeeService by lazy {
        RetrofitInstance.create(AssessmentFeeService::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrarFeeTab1Binding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Show Loading Dialog
        loadingDialog = DialogUtils.showLoading(requireActivity())
        loadingDialog.show()

        // Initialize RecyclerView
        binding.assessmentFeeRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Fetch data from the server
        fetchAssessmentFees()

        binding.addFeeButton.setOnClickListener {
            val detailsDialog = BottomSheetAddAssessmentFragment().apply {
                onDismissListener = {
                    fetchAssessmentFees()
                }
            }
            detailsDialog.show(childFragmentManager, "BottomSheetAddAssessmentFragment")
        }
    }

    private fun fetchAssessmentFees() {
        apiService.getAssessmentFees("getAssessmentFees").enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val assessmentFees = response.body()?.assessment ?: emptyList()

                    // Check if data exists before setting the adapter
                    if (assessmentFees.isNotEmpty()) {
                        val adapter = AssessmentFeeAdapter(
                            assessmentFees.toMutableList(),
                            deleteListener = { fee ->
                                // On delete button click, show confirmation
                                showDeleteConfirmation(fee)
                            },
                            editListener = { fee ->
                                // On edit button click, open the bottom sheet in edit mode
                                val editDialog = BottomSheetAddAssessmentFragment().apply {
                                    arguments = Bundle().apply {
                                        putInt("id", fee.id)
                                        putString("title", fee.title)
                                        putDouble("amount", fee.amount)
                                        putString("description", fee.description)
                                        putInt("subFee", fee.subFee)
                                    }
                                    onDismissListener = {
                                        fetchAssessmentFees()
                                    }
                                }
                                editDialog.show(childFragmentManager, "BottomSheetAddAssessmentFragment")
                            }
                        )
                        binding.assessmentFeeRecyclerView.adapter = adapter
                    } else {
                        Toast.makeText(context, "No assessment fees available", Toast.LENGTH_SHORT).show()
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
                Log.e("RegistrarFeeTab1", "Error: ${t.message}")
            }
        })
    }

    private fun showDeleteConfirmation(fee: AssessmentFee) {
        SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
            .setTitleText("Delete Assessment Fee")
            .setContentText("Are you sure you want to delete this assessment fee?")
            .setConfirmText("Yes")
            .setCancelText("No")
            .setConfirmClickListener { dialog ->
                dialog.dismissWithAnimation()
                deleteAssessmentFee(fee)
            }
            .setCancelClickListener { dialog ->
                dialog.dismissWithAnimation()
            }
            .show()
    }

    private fun deleteAssessmentFee(fee: AssessmentFee) {
        val requestBody = mapOf("id" to fee.id)

        apiService.deleteAssessmentFee(requestBody).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    Toast.makeText(context, "Fee deleted successfully", Toast.LENGTH_SHORT).show()
                    // Update the UI after successful deletion
                    (binding.assessmentFeeRecyclerView.adapter as AssessmentFeeAdapter).deleteItem(fee)
                } else {
                    Toast.makeText(context, "Failed to delete fee", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("RegistrarFeeTab1", "Error: ${t.message}")
            }
        })
    }
}
