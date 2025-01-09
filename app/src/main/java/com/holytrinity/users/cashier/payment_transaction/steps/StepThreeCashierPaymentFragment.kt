package com.holytrinity.users.cashier.payment_transaction.steps

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.holytrinity.api.BeneFactorService
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.databinding.FragmentStepThreeCashierPaymentBinding
import com.holytrinity.model.BeneResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StepThreeCashierPaymentFragment : Fragment() {
    private lateinit var binding: FragmentStepThreeCashierPaymentBinding
    private lateinit var viewModel: ViewModelPayment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStepThreeCashierPaymentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[ViewModelPayment::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.studentIDTextView.text = viewModel.studentID
        binding.studentNameTextView.text = viewModel.student_name

        getAllBenefactor(viewModel.studentID)
        getTransactionModes()
        binding.amountInputEditLayout.addTextChangedListener {
            viewModel.amountPay = it.toString()
        }

    }
    private fun getAllBenefactor(studentID: String) {
        val service = RetrofitInstance.create(BeneFactorService::class.java)
        service.getBenefactorName(studentID)!!.enqueue(object : Callback<BeneResponse> {
            override fun onResponse(call: Call<BeneResponse>, response: Response<BeneResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val responseData = response.body()
                    if (responseData?.error == null) {
                        val benefactorName = responseData!!.name
                        val benefactorID = responseData.benefactor_id // Assuming 'id' is part of the BeneResponse model
                        Log.d("Benefactor Name", "Name: $benefactorName, ID: $benefactorID")

                        val dropdownItems = mutableListOf<Pair<String, String>>() // Pair of ID and Name
                        dropdownItems.add("12" to "Select Benefactor") // Add default blank option with ID 0
                        if (benefactorName != null && benefactorID != null) {
                            dropdownItems.add(benefactorID.toString() to benefactorName)
                        }

                        // Extract only names for the Spinner display
                        val benefactorNames = dropdownItems.map { it.second }

                        val adapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_item,
                            benefactorNames
                        )
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        binding.paymentBenefactorSpinner.adapter = adapter

                        // Set a listener to update ViewModel with the selected benefactor ID
                        binding.paymentBenefactorSpinner.onItemSelectedListener = object :
                            AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                val selectedBenefactorId = dropdownItems[position].first
                                if (selectedBenefactorId != "12") {
                                    // Update ViewModel with the valid benefactor ID
                                    viewModel.benefactor_id = selectedBenefactorId.toInt()
                                    Log.d("Selected Benefactor", "ID: ${viewModel.benefactor_id}")
                                } else {
                                    // If "Select Benefactor" is selected, set benefactor_id to 0
                                    viewModel.benefactor_id = 12
                                    Log.d("Selected Benefactor", "No valid ID selected, default to 0")
                                }
                            }

                            override fun onNothingSelected(parent: AdapterView<*>) {
                                // Handle case where no selection is made, should not happen with default option
                                viewModel.benefactor_id = 12 // Default to 0 if nothing selected
                            }
                        }
                    } else {
                        Log.e("Error", responseData.error ?: "Unknown error")
                        // In case of error, we set the default value (0) in the ViewModel
                        viewModel.benefactor_id = 12
                    }
                } else {
                    Log.e("Error", "Failed to fetch data")
                    // Set the default value (0) in case of failure to fetch data
                    viewModel.benefactor_id = 12
                }
            }

            override fun onFailure(call: Call<BeneResponse>, t: Throwable) {
                Log.e("Error", "Network error: ${t.message}")
                // Set the default value (0) in case of network failure
                viewModel.benefactor_id = 12
            }
        })
    }

    private fun getTransactionModes() {
        val modes = listOf("Cash", "Credit", "Debit", "Online")
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            modes
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.modeOfTransactionSpinner.adapter = adapter

        val defaultMode = viewModel.transaction
        val defaultPosition = modes.indexOf(defaultMode)
        if (defaultPosition >= 0) {
            binding.modeOfTransactionSpinner.setSelection(defaultPosition)
        }
             binding.modeOfTransactionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                viewModel.transaction = modes[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }

}
