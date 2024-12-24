package com.holytrinity.users.registrar.studentledger

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.canorecoapp.utils.DialogUtils
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.api.SoaService
import com.holytrinity.api.StudentService
import com.holytrinity.databinding.FragmentStatementsOfAccountsLedgerBinding
import com.holytrinity.model.Soa
import com.holytrinity.model.Student
import com.holytrinity.users.registrar.adapter.SoaAdapter
import com.holytrinity.users.registrar.adapter.SoaAdapter2
import retrofit2.Call
import retrofit2.Callback


class StatementsOfAccountsLedgerFragment : Fragment() {
    private lateinit var binding: FragmentStatementsOfAccountsLedgerBinding
    private lateinit var soaList: List<Soa>
    private lateinit var loadingDialog: SweetAlertDialog
    private lateinit var soaAdapter: SoaAdapter2
    private lateinit var students: List<Student>
    private lateinit var studentNamesMap: MutableMap<String, String>
    private lateinit var studentNames: MutableMap<String, String>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStatementsOfAccountsLedgerBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        studentNames = mutableMapOf()
        loadingDialog = DialogUtils.showLoading(requireActivity())
        loadingDialog.show()

        getAllStudents()
    }
    private fun getAllStudents() {
        val studentService = RetrofitInstance.create(StudentService::class.java)
        studentService.getStudents().enqueue(object : Callback<List<Student>> {
            override fun onResponse(call: Call<List<Student>>, response: retrofit2.Response<List<Student>>) {
                if (response.isSuccessful) {
                    students = response.body() ?: emptyList()
                    students.forEach { student ->
                        Log.d("StudentData", "ID: ${student.student_id}, Name: ${student.student_name}")
                    }
                    studentNamesMap = students.associate { it.student_name.toString() to it.student_id.toString() }.toMutableMap()
                    loadingDialog.dismiss()
                    setupAutoCompleteTextView()
                } else {
                    loadingDialog.dismiss()
                    Log.e("Error", "Failed to fetch students: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Student>>, t: Throwable) {
                Log.e("Error", "Failed to fetch students: ${t.message}")
                loadingDialog.dismiss()
            }
        })
    }

    private fun setupAutoCompleteTextView() {
        val studentNamesList = studentNamesMap.keys.toList()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, studentNamesList)
        binding.searchStudentTextView.setAdapter(adapter)
        binding.searchStudentTextView.setOnItemClickListener { parent, _, position, _ ->
            val selectedName = parent.getItemAtPosition(position) as String
            val selectedStudentID = studentNamesMap[selectedName]
            val selectedStudent = students.find { it.student_id.toString() == selectedStudentID }

            if (selectedStudent != null) {
                binding.studentInfoLayout.visibility = View.VISIBLE

                binding.linear.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.VISIBLE

                binding.pdfLayout.visibility = View.VISIBLE
                binding.recyclerView.visibility = View.VISIBLE
                binding.studentNumber.text = selectedStudent.student_id.toString()
                binding.studentName.text = selectedStudent.student_name

                // Fetch the SOA for the selected student and update the RecyclerView
                fetchAllSoa(selectedStudent.student_id.toString())
            }
        }
    }

    private fun fetchAllSoa(studentId: String? = null) {
        val service = RetrofitInstance.create(SoaService::class.java)
        service.getAllSoa(studentId).enqueue(object : Callback<List<Soa>> {
            override fun onResponse(call: Call<List<Soa>>, response: retrofit2.Response<List<Soa>>) {
                if (response.isSuccessful) {
                    soaList = response.body() ?: emptyList()
                    if (soaList.isNotEmpty()) {
                        // Update the adapter with the new data and refresh the RecyclerView

                        searchStudents()
                    } else {
                        Log.e("No Data", "No SOA records found for student ID: $studentId")
                    }
                    loadingDialog.dismiss()
                } else {
                    Log.e("Error", "Failed to fetch SOA: ${response.code()}")
                    loadingDialog.dismiss()
                }
            }

            override fun onFailure(call: Call<List<Soa>>, t: Throwable) {
                Log.e("Error", "Failed to fetch SOA: ${t.message}")
                loadingDialog.dismiss()
            }
        })
    }

    private fun searchStudents() {
        val studentService = RetrofitInstance.create(StudentService::class.java)
        studentService.getStudents().enqueue(object : Callback<List<Student>> {
            override fun onResponse(
                call: Call<List<Student>>,
                response: retrofit2.Response<List<Student>>
            ) {
                if (response.isSuccessful) {
                    val students = response.body() ?: emptyList()
                    students.forEach { student ->
                        studentNames[student.student_id.toString()] = student.student_name.toString()
                    }
                    if (::soaAdapter.isInitialized) {
                        soaAdapter.updateSoaList(soaList)
                    } else {
                        setupRecyclerView(soaList)
                    }
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

    private fun setupRecyclerView(soaList: List<Soa>) {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        soaAdapter = SoaAdapter2(soaList, studentNames,false)
        binding.recyclerView.adapter = soaAdapter
    }
}
