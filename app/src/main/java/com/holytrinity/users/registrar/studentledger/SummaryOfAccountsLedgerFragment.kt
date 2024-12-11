package com.holytrinity.users.registrar.studentledger

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.canorecoapp.utils.DialogUtils
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.api.SoaService
import com.holytrinity.api.StudentService
import com.holytrinity.databinding.FragmentSummaryOfAccountsLedgerBinding
import com.holytrinity.model.Soa
import com.holytrinity.model.Student
import com.holytrinity.users.registrar.adapter.SoaAdapter
import retrofit2.Call
import retrofit2.Callback

class SummaryOfAccountsLedgerFragment : Fragment() {
    private lateinit var binding : FragmentSummaryOfAccountsLedgerBinding
    private lateinit var soaList: List<Soa>
    private lateinit var studentNames: MutableMap<String, String>
    private lateinit var loadingDialog: SweetAlertDialog
    private lateinit var soaAdapter: SoaAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSummaryOfAccountsLedgerBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        studentNames = mutableMapOf()
        loadingDialog = DialogUtils.showLoading(requireActivity())
        loadingDialog.show()

        binding.searchStudentTextView.addTextChangedListener { text ->
            soaAdapter.filter(text.toString())
        }
        fetchAllSoa()
    }
    private fun fetchAllSoa(studentId: String? = null) {
        val service = RetrofitInstance.create(SoaService::class.java)
        service.getAllSoa(studentId).enqueue(object : Callback<List<Soa>> {
            override fun onResponse(call: Call<List<Soa>>, response: retrofit2.Response<List<Soa>>) {
                if (response.isSuccessful) {
                    soaList = response.body()!!
                    fetchAllStudents()
                } else {
                    Log.e("Error", "Failed to fetch SOA: ${response.code()}")
                    //    binding.text.text = "Error fetching SOA."
                    loadingDialog.dismiss()
                    showErrorDialog("Failed to fetch SOA data.")
                }
            }

            override fun onFailure(call: Call<List<Soa>>, t: Throwable) {
                Log.e("Error", "Failed to fetch SOA: ${t.message}")
                //  binding.text.text = "Error fetching SOA.
                   loadingDialog.dismiss()
            }
        })
    }
    private fun showErrorDialog(message: String) {
        SweetAlertDialog(requireContext(), SweetAlertDialog.ERROR_TYPE)
            .setTitleText("Error")
            .setContentText(message)
            .show()
    }
    private fun fetchAllStudents() {
        val studentService = RetrofitInstance.create(StudentService::class.java)
        studentService.getStudents().enqueue(object : Callback<List<Student>> {
            override fun onResponse(
                call: Call<List<Student>>,
                response: retrofit2.Response<List<Student>>
            ) {
                if (response.isSuccessful) {
                    val students = response.body() ?: emptyList()

                    // Populate the studentNames map with studentId to studentName
                    students.forEach { student ->
                        // Access student properties directly without casting
                        studentNames[student.studentID] = student.name
                    }
                    setupRecyclerView()
                } else {
                    loadingDialog.dismiss()
                    Log.e("Error", "Failed to fetch students: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Student>>, t: Throwable) {
                loadingDialog.dismiss()
                Log.e("Error", "Failed to fetch students: ${t.message}")
            }
        })
    }


    private fun setupRecyclerView() {
        binding.recyclerSummary.layoutManager = LinearLayoutManager(requireContext())
        if (!::soaAdapter.isInitialized) {
            soaAdapter = SoaAdapter(soaList, studentNames)
        }
        binding.recyclerSummary.adapter = soaAdapter
        loadingDialog.dismiss()
    }
}