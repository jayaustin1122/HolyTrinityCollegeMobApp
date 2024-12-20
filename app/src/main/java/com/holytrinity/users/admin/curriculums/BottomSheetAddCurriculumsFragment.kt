package com.holytrinity.users.admin.curriculums

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
import com.holytrinity.api.Curriculum
import com.holytrinity.api.CurriculumService
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.databinding.FragmentBottomSheetAddCurriculumsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BottomSheetAddCurriculumsFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomSheetAddCurriculumsBinding? = null
    private val binding get() = _binding!!
    private val apiService: CurriculumService by lazy {
        RetrofitInstance.create(CurriculumService::class.java)
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
        _binding = FragmentBottomSheetAddCurriculumsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Check if arguments are passed (means edit mode)
        arguments?.let {
            if (it.containsKey("curriculum_id")) {
                editMode = true
                editId = it.getInt("curriculum_id")
                binding.codeEditText.setText(it.getString("code"))
                binding.titleEditText.setText(it.getString("name"))
                binding.descriptionEditText.setText(it.getString("description"))
                binding.doneButton.text = "Update"
                binding.titleTextView.text = "Edit Curriculum"
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

        val confirmTitle = if (editMode) "Update Curriculum" else "Add Curriculum"
        val confirmContent = if (editMode) {
            "Click \"Done\" to update this curriculum."
        } else {
            "Click \"Done\" to add this new curriculum."
        }

        SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
            .setTitleText(confirmTitle)
            .setContentText(confirmContent)
            .setConfirmText("Done")
            .setCancelText("Cancel")
            .setConfirmClickListener { dialog ->
                dialog.dismissWithAnimation()
                if (editMode && editId != null) {
                    updateCurriculum(editId!!, code, name, description)
                } else {
                    addCurriculum(code, name, description)
                }
            }
            .setCancelClickListener { dialog ->
                dialog.dismissWithAnimation()
                Log.d("AddCurriculumFragment", "Add/Update Cancelled")
            }
            .show()
    }

    private fun addCurriculum(code: String, name: String, description: String) {
        val curriculum = com.holytrinity.model.Curriculum(code, name, description)

        apiService.addCurriculum(curriculum).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    DialogUtils.showSuccessMessage(
                        requireActivity(),
                        "Success",
                        "Curriculum Added Successfully"
                    ).show()
                    dismiss()
                } else {
                    Toast.makeText(context, "Failed to add curriculum.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("AddCurriculumFragment", "Error: ${t.message}")
            }
        })
    }

    private fun updateCurriculum(id: Int, code: String, name: String, description: String) {
        val curriculum = Curriculum(id, code, name, description )

        apiService.updateCurriculum(curriculum).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful && response.body()?.status == "success") {
                    DialogUtils.showSuccessMessage(
                        requireActivity(),
                        "Success",
                        "Curriculum Updated Successfully"
                    ).show()
                    dismiss()
                } else {
                    Toast.makeText(context, "Failed to update curriculum.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("UpdateCurriculumFragment", "Error: ${t.message}")
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
