package com.holytrinity.users.admin.courses

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.holytrinity.R
import com.holytrinity.api.ApiResponse
import com.holytrinity.api.Courses
import com.holytrinity.api.CurriculumService
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.databinding.FragmentAdminCoursesBinding
import com.holytrinity.users.registrar.adapter.CoursesAdapter
import com.example.canorecoapp.utils.DialogUtils
import cn.pedant.SweetAlert.SweetAlertDialog
import com.holytrinity.api.CoursesService
import com.holytrinity.api.Curriculum
import com.holytrinity.users.admin.curriculums.BottomSheetAddCurriculumsFragment
import com.holytrinity.users.registrar.adapter.CurriculumAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminCoursesFragment : Fragment() {

    private lateinit var binding: FragmentAdminCoursesBinding
    private lateinit var loadingDialog: SweetAlertDialog
    private val apiService: CoursesService by lazy {
        RetrofitInstance.create(CoursesService::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentAdminCoursesBinding.inflate(inflater, container, false)
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
        fetchCourses()

        binding.addFabButton.setOnClickListener {
            val detailsDialog = BottomSheetAddCoursesFragment().apply {
                onDismissListener = {
                    fetchCourses()
                }
            }
            detailsDialog.show(childFragmentManager, "BottomSheetAddCourseFragment")
        }
        binding.toolbarBackButton.setOnClickListener {
            findNavController().apply {
                navigate(R.id.adminOfferingsFragment)
            }
        }
    }

    private fun fetchCourses() {
        apiService.getCourses("getCourses").enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val courses = response.body()?.courses ?: emptyList()

                    // Check if data exists before setting the adapter
                    if (courses.isNotEmpty()) {
                        val adapter = CoursesAdapter(
                            courses.toMutableList(),
                            deleteListener = { cour ->
                                // On delete button click, show confirmation
                                showDeleteConfirmation(cour)
                            },
                            editListener = { cour ->
                                // On edit button click, open the bottom sheet in edit mode
                                val editDialog = BottomSheetAddCoursesFragment().apply {
                                    arguments = Bundle().apply {
                                        putInt("course_id", cour.course_id)
                                        putString("name", cour.name)
                                        putString("code", cour.code)
                                        putString("description", cour.description)
                                    }
                                    onDismissListener = {
                                        fetchCourses()
                                    }
                                }
                                editDialog.show(childFragmentManager, "BottomSheetAddCoursesFragment")
                            }
                        )
                        binding.recyclerView.adapter = adapter
                    } else {
                        Toast.makeText(context, "No Course available", Toast.LENGTH_SHORT).show()
                    }

                    loadingDialog.dismiss()
                } else {
                    loadingDialog.dismiss()
                    Toast.makeText(context, "Failed to fetch Course", Toast.LENGTH_SHORT).show()
                }
            }

            private fun showDeleteConfirmation(fee: Courses) {
                SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Delete Course Fee")
                    .setContentText("Are you sure you want to delete this Course?")
                    .setConfirmText("Yes")
                    .setCancelText("No")
                    .setConfirmClickListener { dialog ->
                        dialog.dismissWithAnimation()
                        deelteCourses(fee)
                    }
                    .setCancelClickListener { dialog ->
                        dialog.dismissWithAnimation()
                    }
                    .show()
            }

            private fun deelteCourses(fee: Courses) {
                val requestBody = mapOf("course_id" to fee.course_id)

                apiService.deleteCourses(requestBody).enqueue(object : Callback<ApiResponse> {
                    override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                        if (response.isSuccessful && response.body()?.status == "success") {
                            Toast.makeText(context, "Course deleted successfully", Toast.LENGTH_SHORT).show()
                            // Update the UI after successful deletion
                            (binding.recyclerView.adapter as CoursesAdapter).deleteItem(fee)
                        } else {
                            Toast.makeText(context, "Failed to delete Course", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                        Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                        Log.e("Course", "Error: ${t.message}")
                    }
                })
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                loadingDialog.dismiss()
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("Course", "Error: ${t.message}")
            }
        })
    }
}
