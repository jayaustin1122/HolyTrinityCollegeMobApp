package com.holytrinity.users.admin.subjects

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
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.api.Subject
import com.holytrinity.api.SubjectService
import com.holytrinity.databinding.FragmentAdminSubjectsBinding
import com.holytrinity.users.registrar.adapter.SubjectAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminSubjectsFragment : Fragment() {

    private lateinit var loadingDialog: SweetAlertDialog
    private lateinit var binding: FragmentAdminSubjectsBinding
    private val apiService: SubjectService by lazy {
        RetrofitInstance.create(SubjectService::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminSubjectsBinding.inflate(layoutInflater)
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
        fetchSubjects()

        binding.addFabButton.setOnClickListener {
            val detailsDialog = BottomSheetAddSubjectFragment().apply {
                onDismissListener = {
                    fetchSubjects()
                }
            }
            detailsDialog.show(childFragmentManager, "BottomSheetAddSubjectFragment")
        }
        binding.toolbarBackButton.setOnClickListener {
            findNavController().apply {
                navigate(R.id.adminOfferingsFragment)
            }
        }
    }

    private fun fetchSubjects() {
        apiService.getSubjects("getSubjects").enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val subject = response.body()?.subject ?: emptyList()

                    // Check if data exists before setting the adapter
                    if (subject.isNotEmpty()) {
                        val adapter = SubjectAdapter(
                            subject.toMutableList(),
                            deleteListener = { sub ->
                                // On delete button click, show confirmation
                                showDeleteConfirmation(sub)
                            },
                            editListener = { sub ->
                                // On edit button click, open the bottom sheet in edit mode
                                val editDialog = BottomSheetAddSubjectFragment().apply {
                                    arguments = Bundle().apply {
                                        putInt("subject_id", sub.subject_id)
                                        putString("name", sub.name)
                                        putString("code", sub.code)
                                        putString("units", sub.units.toString())
                                    }
                                    onDismissListener = {
                                        fetchSubjects()
                                    }
                                }
                                editDialog.show(
                                    childFragmentManager,
                                    "BottomSheetAddSubjectFragment"
                                )
                            }
                        )
                        binding.recyclerView.adapter = adapter
                    } else {
                        Toast.makeText(context, "No subject available", Toast.LENGTH_SHORT)
                            .show()
                    }

                    loadingDialog.dismiss()
                } else {
                    loadingDialog.dismiss()
                    Toast.makeText(context, "Failed to fetch subject", Toast.LENGTH_SHORT).show()
                }
            }

            private fun showDeleteConfirmation(fee: Subject) {
                SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Delete Subject")
                    .setContentText("Are you sure you want to delete this subject?")
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

            private fun deleteCurriculum(fee: Subject) {
                val requestBody = mapOf("subject_id" to fee.subject_id)

                apiService.deleteSubject(requestBody).enqueue(object : Callback<ApiResponse> {
                    override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                        if (response.isSuccessful && response.body()?.status == "success") {
                            Toast.makeText(context, "Subject deleted successfully", Toast.LENGTH_SHORT).show()
                            // Update the UI after successful deletion
                            (binding.recyclerView.adapter as SubjectAdapter).deleteItem(fee)
                        } else {
                            Toast.makeText(context, "Failed to delete Subject", Toast.LENGTH_SHORT).show()
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
                Log.e("AdminSubjects", "Error: ${t.message}")
            }
        })
    }
}