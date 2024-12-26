package com.holytrinity.users.benefactor.beneficiaries

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.holytrinity.R
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.api.SoaService
import com.holytrinity.api.StudentService
import com.holytrinity.databinding.FragmentBeneficiariesDetailsBinding
import com.holytrinity.model.Soa
import com.holytrinity.model.Student
import com.holytrinity.users.registrar.adapter.SoaAdapter2
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BeneficiariesDetailsFragment : Fragment() {
    private lateinit var binding: FragmentBeneficiariesDetailsBinding

    // For demonstration
    private lateinit var soaAdapter: SoaAdapter2
    private var soaList: List<Soa> = emptyList()
    private var studentNames: MutableMap<String, String> = mutableMapOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBeneficiariesDetailsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbarBackButton.setOnClickListener {
            findNavController().navigateUp() // or whichever navigation
        }

        // 1) Get the "student_id" from arguments
        val studentId = arguments?.getString("student_id") ?: ""
        Log.d("BeneficiariesDetails", "Received student_id = $studentId")

        // 2) Now fetch the SOA for this student
        fetchAllSoa(studentId)
    }

    private fun fetchAllSoa(studentId: String) {
        val service = RetrofitInstance.create(SoaService::class.java)
        service.getAllSoa(studentId).enqueue(object : Callback<List<Soa>> {
            override fun onResponse(call: Call<List<Soa>>, response: Response<List<Soa>>) {
                if (response.isSuccessful) {
                    soaList = response.body() ?: emptyList()
                    if (soaList.isNotEmpty()) {
                        // Once we have SOA list, we can also load student data
                        searchStudents()
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

    private fun searchStudents() {
        val studentService = RetrofitInstance.create(StudentService::class.java)
        studentService.getStudents().enqueue(object : Callback<List<Student>> {
            override fun onResponse(call: Call<List<Student>>, response: Response<List<Student>>) {
                if (response.isSuccessful) {
                    val students = response.body() ?: emptyList()
                    for (stud in students) {
                        // Map student ID to student name
                        studentNames[stud.student_id.toString()] = stud.student_name.toString()
                    }
                    if (::soaAdapter.isInitialized) {
                        soaAdapter.updateSoaList(soaList)
                    } else {
                        setupRecyclerView(soaList)
                    }
                } else {
                    Log.e("Error", "Failed to fetch students: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Student>>, t: Throwable) {
                Log.e("Error", "Failed to fetch students: ${t.message}")
            }
        })
    }

    private fun setupRecyclerView(soaList: List<Soa>) {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        soaAdapter = SoaAdapter2(soaList, studentNames, false)
        binding.recyclerView.adapter = soaAdapter
    }
}
