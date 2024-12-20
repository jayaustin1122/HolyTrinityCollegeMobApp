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
import retrofit2.Response

class SummaryOfAccountsLedgerFragment : Fragment() {
    private lateinit var binding: FragmentSummaryOfAccountsLedgerBinding
    private var soaList: List<Soa> = emptyList()
    private lateinit var studentNames: MutableMap<String, String>
    private lateinit var loadingDialog: SweetAlertDialog
    private lateinit var soaAdapter: SoaAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSummaryOfAccountsLedgerBinding.inflate(inflater, container, false)
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

        binding.btnFilter.setOnClickListener {
            val filterFragment = BottomSheetFilterSoaFragment { department ->
                applyFilters(department)
            }
            filterFragment.show(parentFragmentManager, "BottomSheetFilterSoaFragment")
        }



        fetchAllStudents()
    }

    private fun fetchAllStudents() {
        val studentService = RetrofitInstance.create(SoaService::class.java)
        studentService.getAllSoa().enqueue(object : Callback<List<Soa>> {
            override fun onResponse(call: Call<List<Soa>>, response: Response<List<Soa>>) {
                if (response.isSuccessful) {
                    val students = response.body() ?: emptyList()
                    students.forEach { student ->
                        studentNames[student.student_id] = student.student_name
                    }
                    soaList = students
                    setupRecyclerView()
                } else {
                    loadingDialog.dismiss()
                    Log.e("Error", "Failed to fetch students: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Soa>>, t: Throwable) {
                loadingDialog.dismiss()
                Log.e("Error", "Failed to fetch students: ${t.message}")
            }
        })
    }

    private fun setupRecyclerView() {
        binding.recyclerSummary.layoutManager = LinearLayoutManager(requireContext())
        soaAdapter = SoaAdapter(soaList, studentNames)
        binding.recyclerSummary.adapter = soaAdapter
        loadingDialog.dismiss()
    }

    private fun applyFilters(department: String) {
        val filteredList = if (department == "All") {
            soaList // If "All" is selected, show all records
        } else {
            soaList.filter { soa ->
                soa.department == department // Filter by selected department
            }
        }
        soaAdapter.updateSoaList(filteredList)
    }
}
