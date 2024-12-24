package com.holytrinity.users.student.due_amount

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.pedant.SweetAlert.SweetAlertDialog
import com.holytrinity.R
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.api.SoaService
import com.holytrinity.databinding.FragmentStudentDueAmountBinding
import com.holytrinity.model.Soa
import com.holytrinity.model.Student
import com.holytrinity.users.registrar.adapter.SoaAdapter2
import com.holytrinity.util.SharedPrefsUtil
import com.holytrinity.util.UserPreferences
import retrofit2.Call
import retrofit2.Callback

class StudentDueAmountFragment : Fragment() {
    private lateinit var binding : FragmentStudentDueAmountBinding
    private lateinit var soaList: List<Soa>
    private lateinit var loadingDialog: SweetAlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStudentDueAmountBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val studentID = "2024026"
        fetchAllSoa(studentID.toString())

    }

    private fun fetchAllSoa(studentId: String? = null) {
        val service = RetrofitInstance.create(SoaService::class.java)
        service.getAllSoa(studentId).enqueue(object : Callback<List<Soa>> {
            override fun onResponse(call: Call<List<Soa>>, response: retrofit2.Response<List<Soa>>) {
                if (response.isSuccessful) {
                    soaList = response.body() ?: emptyList()

                    Log.d("SOAList", "$soaList")

                    // Assuming you want the total due amount to be the sum of all due_amounts
                    val totalDueAmount = soaList.sumOf { it.total_due }

                    Log.d("SOALista", "$totalDueAmount")

                    // Set the value in totalBalanceEditText
                    binding.totalBalanceEditText.setText(String.format("%.2f", totalDueAmount))
                } else {
                    Log.e("SOAList", "Failed to fetch SOA: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Soa>>, t: Throwable) {
                Log.e("Error", "Failed to fetch SOA: ${t.message}")
            }
        })
    }
}