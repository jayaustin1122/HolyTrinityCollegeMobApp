package com.holytrinity.users.cashier.payment_transaction.steps

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.api.SoaService
import com.holytrinity.databinding.FragmentStepTwoCashierPaymentBinding
import com.holytrinity.model.Soa
import com.holytrinity.users.registrar.adapter.SoaAdapter2
import retrofit2.Call
import retrofit2.Callback


class StepTwoCashierPaymentFragment : Fragment() {
    private lateinit var binding : FragmentStepTwoCashierPaymentBinding
    private lateinit var viewModel: ViewModelPayment
    private lateinit var soaAdapter: SoaAdapter2
    private lateinit var soaList: List<Soa>
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
        soaAdapter = SoaAdapter2(emptyList(), emptyMap(),true)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = soaAdapter
        binding.recyclerView.setHasFixedSize(true)
        fetchAllSoa(studentId)
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
                            soaAdapter.updateSoaList(soaList)
                            soaAdapter.notifyDataSetChanged()
                            binding.studentIDTextView.text = student.student_id
                            binding.studentNameTextView.text = student.student_name
                        }

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
}