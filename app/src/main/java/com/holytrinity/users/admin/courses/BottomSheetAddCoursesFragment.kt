package com.holytrinity.users.admin.courses

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.holytrinity.api.ApiResponse
import com.holytrinity.api.Courses
import com.holytrinity.api.CurriculumService
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.databinding.FragmentBottomSheetAddCoursesBinding
import com.example.canorecoapp.utils.DialogUtils
import com.holytrinity.api.CoursesService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BottomSheetAddCoursesFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomSheetAddCoursesBinding? = null
    private val binding get() = _binding!!
    private val apiService: CoursesService by lazy { // Assuming same service interface
        RetrofitInstance.create(CoursesService::class.java)
    }

    // Callback to refresh data on dismissal
    var onDismissListener: (() -> Unit)? = null

    // Variables to determine if we are in edit mode
    private var editMode = false
    private var editId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentBottomSheetAddCoursesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Check if arguments are passed (means edit mode)
        arguments?.let {
            if (it.containsKey("course_id")) {
                editMode = true
                editId = it.getInt("id")
                binding.codeEditText.setText(it.getString("code"))
                binding.titleEditText.setText(it.getString("name"))
                binding.descriptionEditText.setText(it.getString("description"))
                binding.doneButton.text = "Update"
                binding.titleTextView.text = "Edit Course"
            }
        }

        binding.doneButton.setOnClickListener {
            validateAndConfirmData()
        }
    }

    private fun validateAndConfirmData() {
        val code = binding.codeEditText.text.toString().trim()
        val name = binding.titleEditText.text.toString().trim()
        val description = binding.descriptionEditText.text.toString().trim()

        if (code.isEmpty() || name.isEmpty() || description.isEmpty()) {
            Toast.makeText(context, "Please fill all fields.", Toast.LENGTH_SHORT).show()
            return
        }

        val confirmTitle = if (editMode) "Update Course" else "Add Course"
        val confirmContent = if (editMode) {
            "Click \"Done\" to update this course."
        } else {
            "Click \"Done\" to add this new course."
        }

        SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
            .setTitleText(confirmTitle)
            .setContentText(confirmContent)
            .setConfirmText("Done")
            .setCancelText("Cancel")
            .setConfirmClickListener { dialog ->
                dialog.dismissWithAnimation()
                if (editMode && editId != null) {
                    updateCourse(editId!!, code, name, description)
                } else {
                    addCourse(code, name, description)
                }
            }
            .setCancelClickListener { dialog ->
                dialog.dismissWithAnimation()
                Log.d("AddCourseFragment", "Add/Update Cancelled")
            }
            .show()
    }

    private fun addCourse(code: String, name: String, description: String) {
        val course = com.holytrinity.model.Courses( code, name, description)

        apiService.addCourses(course).enqueue(object : Callback<ApiResponse> { // Assuming addCurriculum handles courses
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    DialogUtils.showSuccessMessage(
                        requireActivity(),
                        "Success",
                        "Course Added Successfully"
                    ).show()
                    dismiss()
                } else {
                    Toast.makeText(context, "Failed to add course.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("AddCourseFragment", "Error: ${t.message}")
            }
        })
    }

    private fun updateCourse(id: Int, code: String, name: String, description: String) {
        val course = Courses( id, code, name, description)

        apiService.updateCourses(course).enqueue(object : Callback<ApiResponse> { // Assuming updateCurriculum handles courses
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    DialogUtils.showSuccessMessage(
                        requireActivity(),
                        "Success",
                        "Course Updated Successfully"
                    ).show()
                    dismiss()
                } else {
                    Toast.makeText(context, "Failed to update course.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("UpdateCourseFragment", "Error: ${t.message}")
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
