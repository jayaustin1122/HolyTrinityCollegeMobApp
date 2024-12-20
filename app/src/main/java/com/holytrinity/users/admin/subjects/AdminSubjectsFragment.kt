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
import com.holytrinity.api.SubjectService
import com.holytrinity.databinding.FragmentAdminSubjectsBinding
import com.holytrinity.users.registrar.adapter.SubjectAdapter
import com.holytrinity.users.registrar.fee_management.BottomSheetAddDiscountFragment
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

        binding.toolbarBackButton.setOnClickListener {
            findNavController().apply {
                navigate(R.id.adminOfferingsFragment)
            }
        }

        // Show Loading Dialog
        loadingDialog = DialogUtils.showLoading(requireActivity())
        loadingDialog.show()

        // Initialize RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Fetch data from the server
        fetchSubjects()

        binding.addFabButton.setOnClickListener {
            val detailsDialog = BottomSheetAddDiscountFragment().apply {
                onDismissListener = {
                    fetchSubjects()
                }
            }
            detailsDialog.show(childFragmentManager, "BottomSheetAddDiscountFragment")
        }
    }

    private fun fetchSubjects() {
        // Show loading indicator if applicable
        loadingDialog.show()

        apiService.getSubjects("getSubjects").enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                loadingDialog.dismiss()
                if (response.isSuccessful && response.body() != null) {
                    val subjects = response.body()?.subject ?: emptyList()

                    // Check if data exists before setting the adapter
                    if (subjects.isNotEmpty()) {
                        val adapter = SubjectAdapter(
                            subjects.toMutableList(),
                            deleteListener = { subject ->
                                // On delete button click, show confirmation
                                //showDeleteConfirmation(subject)
                            },
                            editListener = { subject ->
                                // On edit button click, open the bottom sheet in edit mode
                                val editDialog = BottomSheetAddSubjectFragment().apply { // Ensure this fragment exists
                                    arguments = Bundle().apply {
                                        putInt("id", subject.subject_id)
                                        putString("code", subject.code)
                                        putString("name", subject.name)
                                        putInt("units", subject.units)
                                    }
                                    onDismissListener = {
                                        fetchSubjects() // Refresh the list after editing
                                    }
                                }
                                editDialog.show(childFragmentManager, "BottomSheetAddSubjectFragment")
                            }
                        )
                        binding.recyclerView.adapter = adapter // Ensure you have subjectRecyclerView in your layout
                    } else {
                        Toast.makeText(context, "No subjects available", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Failed to fetch subjects", Toast.LENGTH_SHORT).show()
                    Log.e("SubjectList", "Response unsuccessful or empty")
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                loadingDialog.dismiss()
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("SubjectList", "Error: ${t.message}")
            }
        })
    }

}