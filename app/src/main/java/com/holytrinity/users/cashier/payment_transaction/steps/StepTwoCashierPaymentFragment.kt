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
    private var initialTotalAmount: Double = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStepTwoCashierPaymentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[ViewModelPayment::class.java]

        // Initialize default values in ViewModel
        viewModel.discount_id = 12 // Default discount ID
        viewModel.total = formatCurrency(0.0) // Default total
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val studentId = viewModel.studentID
        soaAdapter = SoaAdapter2(emptyList(), emptyMap(), true)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = soaAdapter
        binding.recyclerView.setHasFixedSize(true)
        binding.studentIDTextView.text = viewModel.studentID
        binding.studentNameTextView.text = viewModel.student_name


        if (viewModel.paymentTitle == "Assessment Fee"){
            binding.linearDiscount.visibility = View.VISIBLE
            binding.coecog.visibility = View.GONE
            fetchAllSoa(studentId)
            fetchDiscounts()
        }
        else{
            binding.addDiscountButton.visibility = View.GONE
            binding.discountName.visibility = View.GONE
            binding.coecog.visibility = View.VISIBLE
            binding.title.text = viewModel.paymentTitle
            binding.Amount.text = viewModel.paymentAmount.toString()
            binding.totalAmountTextView.text = viewModel.paymentAmount.toString()

        }


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
            .setNegativeButton("Cancel") { _, _ ->
                // Ensure ViewModel is updated even if no discount is selected
                clearDiscount()
            }
            .show()
    }

    private fun applyDiscount(discount: Discount) {
        selectedDiscount = discount
        binding.addDiscountButton.text = "${formatCurrency(discount.amount)}"
        binding.discountName.text = discount.title

        // Update the total amount
        val updatedAmount = initialTotalAmount - discount.amount
        val formattedAmount = formatCurrency(updatedAmount)
        binding.totalAmountTextView.text = formattedAmount

        // Update ViewModel
        viewModel.total = formattedAmount
        viewModel.discount_id = discount.discount_id.toInt()
    }

    private fun clearDiscount() {
        selectedDiscount = null
        binding.addDiscountButton.text = "Add Discount"
        binding.discountName.text = "No Discount"

        // Reset total amount to initial
        val formattedAmount = formatCurrency(initialTotalAmount)
        binding.totalAmountTextView.text = formattedAmount

        // Update ViewModel
        viewModel.total = formattedAmount
        viewModel.discount_id = 12 // Default value when no discount is selected
    }

    private fun fetchDiscounts() {
        val service = RetrofitInstance.create(DiscountService::class.java)
        service.getAllDiscounts().enqueue(object : Callback<List<Discount>> {
            override fun onResponse(call: Call<List<Discount>>, response: retrofit2.Response<List<Discount>>) {
                if (response.isSuccessful) {
                    discountList = response.body() ?: emptyList()
                    Log.d("Discounts", "Fetched discounts: $discountList")
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
                        val totalAmount = soaList.sumOf { it.balance }
                        val formattedTotalAmount = formatCurrency(totalAmount)
                        binding.totalAmountTextView.text = formattedTotalAmount
                        initialTotalAmount = totalAmount
                        viewModel.total = formattedTotalAmount
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
