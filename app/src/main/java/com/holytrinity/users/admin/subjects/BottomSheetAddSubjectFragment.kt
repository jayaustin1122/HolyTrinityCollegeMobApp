package com.holytrinity.users.admin.subjects

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
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.api.Subject
import com.holytrinity.api.SubjectService
import com.holytrinity.databinding.FragmentBottomSheetAddSubjectBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BottomSheetAddSubjectFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomSheetAddSubjectBinding? = null
    private val binding get() = _binding!!
    private val apiService: SubjectService by lazy {
        RetrofitInstance.create(SubjectService::class.java)
    }

    // Callback to refresh data on dismissal
    var onDismissListener: (() -> Unit)? = null

    // Variables to determine if we are in edit mode
    private var editMode = false
    private var editId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBottomSheetAddSubjectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Check if arguments are passed (means edit mode)
        arguments?.let {
            if (it.containsKey("subject_id")) {
                editMode = true
                editId = it.getInt("subject_id")
                binding.codeEditText.setText(it.getString("code"))
                binding.titleEditText.setText(it.getString("name"))
                binding.unitsEditText.setText(it.getString("units"))
                binding.doneButton.text = "Update"
                binding.titleTextView.text = "Edit Subject"
            }
        }

        binding.doneButton.setOnClickListener {
            validateAndConfirmData()
        }
    }

    private fun validateAndConfirmData() {
        val code = binding.codeEditText.text.toString().trim()
        val name = binding.titleEditText.text.toString().trim()
        val unitsText = binding.unitsEditText.text.toString().trim()

        if (code.isEmpty() || name.isEmpty() || unitsText.isEmpty()) {
            Toast.makeText(context, "Please fill all fields.", Toast.LENGTH_SHORT).show()
            return
        }
        val units = unitsText.toInt()

        val confirmTitle = if (editMode) "Update Subject" else "Add Subject"
        val confirmContent = if (editMode) {
            "Click \"Done\" to update this Subject."
        } else {
            "Click \"Done\" to add this new subject."
        }

        SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
            .setTitleText(confirmTitle)
            .setContentText(confirmContent)
            .setConfirmText("Done")
            .setCancelText("Cancel")
            .setConfirmClickListener { dialog ->
                dialog.dismissWithAnimation()
                if (editMode && editId != null) {
                    updateSubject(editId!!, code, name, units)
                } else {
                    addSubject(code, name, units)
                }
            }
            .setCancelClickListener { dialog ->
                dialog.dismissWithAnimation()
                Log.d("AddCurriculumFragment", "Add/Update Cancelled")
            }
            .show()
    }


    private fun addSubject(code: String, name: String, units: Int) {
        val subject = com.holytrinity.model.Subject(null,code, name, units,null)

        apiService.addSubject(subject).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    DialogUtils.showSuccessMessage(
                        requireActivity(),
                        "Success",
                        "Subject Added Successfully"
                    ).show()
                    dismiss()
                } else {
                    Toast.makeText(context, "Failed to add Subject.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("AddSubjectFragment", "Error: ${t.message}")
            }
        })
    }

    private fun updateSubject(id: Int, code: String, name: String, units: Int) {
        val subject = Subject(id, code, name, units )

        apiService.updateSubject(subject).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    DialogUtils.showSuccessMessage(
                        requireActivity(),
                        "Success",
                        "Subject Updated Successfully"
                    ).show()
                    dismiss()
                } else {
                    Toast.makeText(context, "Failed to update Subject.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("UpdateSubjectFragment", "Error: ${t.message}")
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