package com.holytrinity.users.cashier.payment_transaction.steps

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.holytrinity.api.DiscountService
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.api.SoaService
import com.holytrinity.databinding.FragmentStepTwoCashierPaymentBinding
import com.holytrinity.model.Discount
import com.holytrinity.model.Soa
import com.holytrinity.users.registrar.adapter.SoaAdapter2
import retrofit2.Call
import retrofit2.Callback
import java.text.NumberFormat
import java.util.Locale


class StepTwoCashierPaymentFragment : Fragment() {
    private lateinit var binding: FragmentStepTwoCashierPaymentBinding
    private lateinit var viewModel: ViewModelPayment
    private lateinit var soaAdapter: SoaAdapter2
    private lateinit var soaList: List<Soa>
    private var discountList: List<Discount> = emptyList()
    private var selectedDiscount: Discount? = null
    private var initialTotalAmount: Double = 0.0  // Store the initial total amount

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStepTwoCashierPaymentBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[ViewModelPayment::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val studentId = viewModel.studentID
        soaAdapter = SoaAdapter2(emptyList(), emptyMap(), true)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = soaAdapter
        binding.recyclerView.setHasFixedSize(true)
        fetchAllSoa(studentId)
        fetchDiscounts()

        // Store the initial total when the fragment is first created
        binding.addDiscountButton.setOnClickListener {
            showDiscountDialog()
        }
    }

    private fun showDiscountDialog() {
        val discountTitles = discountList.map { it.title }.toTypedArray()

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Select a Discount")
            .setItems(discountTitles) { _, which ->
                val discount = discountList[which]
                applyDiscount(discount)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun applyDiscount(discount: Discount) {
        selectedDiscount = discount
        binding.addDiscountButton.text = "${formatCurrency(discount.amount)}"
        binding.discountName.text = discount.title
        var totalAmount = initialTotalAmount
        val updatedAmount = totalAmount - discount.amount
        val formattedAmount = formatCurrency(updatedAmount)
        binding.totalAmountTextView.text = formattedAmount
        viewModel.total = formattedAmount
    }

    private fun fetchDiscounts() {
        val service = RetrofitInstance.create(DiscountService::class.java)
        service.getAllDiscounts().enqueue(object : Callback<List<Discount>> {
            override fun onResponse(call: Call<List<Discount>>, response: retrofit2.Response<List<Discount>>) {
                if (response.isSuccessful) {
                    discountList = response.body() ?: emptyList()
                } else {
                    Log.e("Error", "Failed to fetch discounts")
                }
            }

            override fun onFailure(call: Call<List<Discount>>, t: Throwable) {
                Log.e("Error", "Failed to fetch discounts: ${t.message}")
            }
        })
    }

    private fun fetchAllSoa(studentId: String? = null) {
        val service = RetrofitInstance.create(SoaService::class.java)
        service.getAllSoa(studentId).enqueue(object : Callback<List<Soa>> {
            override fun onResponse(call: Call<List<Soa>>, response: retrofit2.Response<List<Soa>>) {
                if (response.isSuccessful) {
                    soaList = response.body() ?: emptyList()
                    if (soaList.isNotEmpty()) {
                        soaList.forEach { student ->
                            Log.d("StudentData", "ID: ${student.student_id}, Name: ${student.student_name}")
                        }
                        val totalAmount = soaList.sumOf { it.balance }
                        val formattedTotalAmount = formatCurrency(totalAmount)
                        binding.totalAmountTextView.text = formattedTotalAmount
                        viewModel.total = formattedTotalAmount

                        // Store the initial total amount
                        initialTotalAmount = totalAmount

                        soaAdapter.updateSoaList(soaList)
                        soaAdapter.notifyDataSetChanged()
                        binding.studentIDTextView.text = soaList.firstOrNull()?.student_id ?: ""
                        binding.studentNameTextView.text = soaList.firstOrNull()?.student_name ?: ""
                    } else {
                        Log.e("No Data", "No SOA records found for student ID: $studentId")
                    }
                } else {
                    Log.e("Error", "Failed to fetch SOA: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Soa>>, t: Throwable) {
                Log.e("Error", "Failed to fetch SOA: ${t.message}")
            }
        })
    }

    private fun formatCurrency(amount: Double): String {
        val format = NumberFormat.getCurrencyInstance(Locale("en", "PH"))
        return format.format(amount)
    }
}
