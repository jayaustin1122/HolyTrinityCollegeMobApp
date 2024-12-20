package com.holytrinity.users.admin.curriculums

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.canorecoapp.utils.DialogUtils
import com.holytrinity.R
import com.holytrinity.api.ApiResponse
import com.holytrinity.api.Curriculum
import com.holytrinity.api.CurriculumService
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.databinding.FragmentAdminCurriculumsBinding
import com.holytrinity.users.registrar.adapter.CurriculumAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminCurriculumsFragment : Fragment() {

    private lateinit var loadingDialog: SweetAlertDialog
    private lateinit var binding: FragmentAdminCurriculumsBinding
    private val apiService: CurriculumService by lazy {
        RetrofitInstance.create(CurriculumService::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminCurriculumsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Show Loading Dialog
        loadingDialog = DialogUtils.showLoading(requireActivity())
        loadingDialog.show()

        // Initialize RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Fetch data from the server
        fetchDiscountFees()

        binding.addFabButton.setOnClickListener {
            val detailsDialog = BottomSheetAddCurriculumsFragment().apply {
                onDismissListener = {
                    fetchDiscountFees()
                }
            }
            detailsDialog.show(childFragmentManager, "BottomSheetAddCurriculumsFragment")
        }
        binding.toolbarBackButton.setOnClickListener {
            findNavController().apply {
                navigate(R.id.adminOfferingsFragment)
            }
        }
    }

    private fun fetchDiscountFees() {
        apiService.getCurriculums("getCurriculums").enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val curriculum = response.body()?.curriculum ?: emptyList()

                    // Check if data exists before setting the adapter
                    if (curriculum.isNotEmpty()) {
                        val adapter = CurriculumAdapter(
                            curriculum.toMutableList(),
                            deleteListener = { curr ->
                                // On delete button click, show confirmation
                                showDeleteConfirmation(curr)
                            },
                            editListener = { curr ->
                                // On edit button click, open the bottom sheet in edit mode
                                val editDialog = BottomSheetAddCurriculumsFragment().apply {
                                    arguments = Bundle().apply {
                                        putInt("curriculum_id", curr.curriculum_id)
                                        putString("name", curr.name)
                                        putString("code", curr.code)
                                        putString("description", curr.description)
                                    }
                                    onDismissListener = {
                                        fetchDiscountFees()
                                    }
                                }
                                editDialog.show(childFragmentManager, "BottomSheetAddAssessmentFragment")
                            }
                        )
                        binding.recyclerView.adapter = adapter
                    } else {
                        Toast.makeText(context, "No curriculums available", Toast.LENGTH_SHORT).show()
                    }

                    loadingDialog.dismiss()
                } else {
                    loadingDialog.dismiss()
                    Toast.makeText(context, "Failed to fetch fees", Toast.LENGTH_SHORT).show()
                }
            }

            private fun showDeleteConfirmation(fee: Curriculum) {
                SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Delete Discount Fee")
                    .setContentText("Are you sure you want to delete this discount fee?")
                    .setConfirmText("Yes")
                    .setCancelText("No")
                    .setConfirmClickListener { dialog ->
                        dialog.dismissWithAnimation()
                        deleteCurriculum(fee)
                    }
                    .setCancelClickListener { dialog ->
                        dialog.dismissWithAnimation()
                    }
                    .show()
            }

            private fun deleteCurriculum(fee: Curriculum) {
                val requestBody = mapOf("curriculum_id" to fee.curriculum_id)

                apiService.deleteCurriculum(requestBody).enqueue(object : Callback<ApiResponse> {
                    override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                        if (response.isSuccessful && response.body()?.status == "success") {
                            Toast.makeText(context, "Curriculum deleted successfully", Toast.LENGTH_SHORT).show()
                            // Update the UI after successful deletion
                            (binding.recyclerView.adapter as CurriculumAdapter).deleteItem(fee)
                        } else {
                            Toast.makeText(context, "Failed to delete Curriculum", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                        Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                        Log.e("RegistrarFeeTab2", "Error: ${t.message}")
                    }
                })
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                loadingDialog.dismiss()
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("RegistrarFeeTab2", "Error: ${t.message}")
            }
        })
    }

}