package com.holytrinity.users.registrar.fee_management

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.canorecoapp.utils.DialogUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.holytrinity.api.ApiResponse
import com.holytrinity.api.AssessmentFeeService
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.databinding.FragmentBottomSheetAddAssessmentBinding
import com.holytrinity.model.AssessmentFee
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BottomSheetAddAssessmentFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomSheetAddAssessmentBinding? = null
    private val binding get() = _binding!!
    private val apiService: AssessmentFeeService by lazy {
        RetrofitInstance.create(AssessmentFeeService::class.java)
    }

    private val subFeeMap = mutableMapOf<String, Int>()

    // Callback to refresh data on dismissal
    var onDismissListener: (() -> Unit)? = null

    // Variables to determine if we are in edit mode
    private var editMode = false
    private var editId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBottomSheetAddAssessmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fetchSubFeeData()

        // Check if arguments are passed (means edit mode)
        arguments?.let {
            if (it.containsKey("id")) {
                editMode = true
                editId = it.getInt("id")
                binding.titleEditText.setText(it.getString("title"))
                binding.amountEditText.setText(it.getDouble("amount").toString())
                binding.descriptionEditText.setText(it.getString("description"))
                // We'll set the spinner value after fetching subFee data.
            }
        }

        binding.doneButton.setOnClickListener {
            validateAndConfirmData()
        }
    }

    private fun fetchSubFeeData() {
        apiService.getSubFeeData("getSubFee").enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val subFees = response.body()?.data ?: emptyList()
                    val subFeeNames = mutableListOf<String>()

                    subFees.forEach { fee ->
                        subFeeMap[fee.title] = fee.id
                        subFeeNames.add(fee.title)
                    }

                    val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, subFeeNames)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.subFeeSpinner.adapter = adapter

                    // If in edit mode, set the spinner to the correct subFee
                    if (editMode && arguments != null) {
                        val subFeeId = arguments!!.getInt("subFee")
                        val subFeeTitle = subFeeMap.entries.find { it.value == subFeeId }?.key
                        if (subFeeTitle != null) {
                            val position = subFeeNames.indexOf(subFeeTitle)
                            if (position >= 0) {
                                binding.subFeeSpinner.setSelection(position)
                            }
                        }
                    }

                } else {
                    Toast.makeText(context, "Failed to fetch sub-fee data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("fetchSubFeeData", "Error: ${t.message}")
            }
        })
    }

    private fun validateAndConfirmData() {
        val title = binding.titleEditText.text.toString().trim()
        val amountText = binding.amountEditText.text.toString().trim()
        val description = binding.descriptionEditText.text.toString().trim()
        val selectedSubFeeName = binding.subFeeSpinner.selectedItem?.toString()

        if (title.isEmpty() || amountText.isEmpty() || description.isEmpty() || selectedSubFeeName == null) {
            Toast.makeText(context, "Please fill all fields.", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountText.toDoubleOrNull()
        if (amount == null) {
            Toast.makeText(context, "Invalid input for Amount", Toast.LENGTH_SHORT).show()
            return
        }

        val subFeeId = subFeeMap[selectedSubFeeName]
        if (subFeeId == null) {
            Toast.makeText(context, "Invalid Sub Fee selection", Toast.LENGTH_SHORT).show()
            return
        }

        val confirmTitle = if (editMode) "Update Assessment Fee" else "Add Assessment Fee"
        val confirmContent = if (editMode) "Click \"Done\" to update this assessment fee." else "Click \"Done\" to add this new assessment fee."

        SweetAlertDialog(requireContext(), SweetAlertDialog.WARNING_TYPE)
            .setTitleText(confirmTitle)
            .setContentText(confirmContent)
            .setConfirmText("Done")
            .setCancelText("Cancel")
            .setConfirmClickListener { dialog ->
                dialog.dismissWithAnimation()
                if (editMode && editId != null) {
                    updateAssessmentFee(editId!!, title, amount, description, subFeeId)
                } else {
                    addAssessmentFee(title, amount, description, subFeeId)
                }
            }
            .setCancelClickListener { dialog ->
                dialog.dismissWithAnimation()
                Log.d("addAssessmentFragment", "Add/Update Cancelled")
            }
            .show()
    }

    private fun addAssessmentFee(title: String, amount: Double, description: String, subFeeId: Int) {
        val assessmentFee = AssessmentFee(title, amount, description, subFeeId)

        apiService.addAssessmentFee(assessmentFee).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    DialogUtils.showSuccessMessage(
                        requireActivity(),
                        "Success",
                        "Assessment Fee Added Successfully"
                    ).show()
                    dismiss()
                } else {
                    Toast.makeText(context, "Failed to insert data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("addAssessmentFee", "Error: ${t.message}")
            }
        })
    }

    private fun updateAssessmentFee(id: Int, title: String, amount: Double, description: String, subFeeId: Int) {
        // We'll reuse the same model (AssessmentFee from model) but we need id as well, so let's create a map or extend the model
        // Let's just create a map for simplicity:
        val assessmentFee =
            com.holytrinity.api.AssessmentFee(id, title, amount, description, subFeeId)

        apiService.updateAssessmentFee(assessmentFee).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful && response.body() != null && response.body()?.status == "success") {
                    DialogUtils.showSuccessMessage(
                        requireActivity(),
                        "Success",
                        "Assessment Fee Updated Successfully"
                    ).show()
                    dismiss()
                } else {
                    Toast.makeText(context, "Failed to update data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("updateAssessmentFee", "Error: ${t.message}")
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
