package com.holytrinity.users.cashier.payment_transaction.steps

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
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
    }

    private fun getAllBenefactor(studentID: String) {
        val service = RetrofitInstance.create(BeneFactorService::class.java)
        service.getBenefactorName(studentID)!!.enqueue(object : Callback<BeneResponse> {
            override fun onResponse(call: Call<BeneResponse>, response: Response<BeneResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    val responseData = response.body()
                    if (responseData?.error == null) {
                        val benefactorName = responseData!!.name
                        Log.d("Benefactor Name", "Name: $benefactorName")

                        val dropdownItems = mutableListOf<String>()
                        dropdownItems.add("") // Add blank value to the first position
                        dropdownItems.add(benefactorName ?: "No Name")

                        val adapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_dropdown_item_1line,
                            dropdownItems
                        )
                        binding.paymentBenefactorSpinner.setAdapter(adapter)
                    } else {
                        Log.e("Error", responseData.error ?: "Unknown error")
                    }
                } else {
                    Log.e("Error", "Failed to fetch data")
                }
            }

            override fun onFailure(call: Call<BeneResponse>, t: Throwable) {
                Log.e("Error", "Network error: ${t.message}")
            }
        })
    }

    // Fetch and set transaction modes to the AutoCompleteTextView
    private fun getTransactionModes() {
        val modes = listOf("Cash", "Credit", "Debit", "Online") // You can replace this with a network call to fetch modes
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            modes
        )
        binding.modeOfTransactionSpinner.setAdapter(adapter)
    }
}
