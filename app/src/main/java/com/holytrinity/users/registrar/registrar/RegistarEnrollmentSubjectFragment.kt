package com.holytrinity.users.registrar.registrar

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.canorecoapp.utils.DialogUtils
import com.holytrinity.R
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.api.StudentService
import com.holytrinity.databinding.DialogStudentDetailsBinding
import com.holytrinity.databinding.FragmentRegistarEnrollmentSubjectBinding
import com.holytrinity.model.Soa
import com.holytrinity.model.Student
import com.holytrinity.model.StudentSolo
import com.holytrinity.users.registrar.adapter.SoaAdapter2
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RegistarEnrollmentSubjectFragment : Fragment() {
    private lateinit var binding : FragmentRegistarEnrollmentSubjectBinding
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
        binding = FragmentRegistarEnrollmentSubjectBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val soaList: List<Soa> = emptyList()
        studentNames = mutableMapOf()

        soaAdapter = SoaAdapter2(soaList, studentNames)

        loadingDialog = DialogUtils.showLoading(requireActivity())
        loadingDialog.show()

        binding.searchStudentTextView.addTextChangedListener { text ->
            soaAdapter.filter(text.toString())
        }

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


                // Fetch the SOA for the selected student and update the RecyclerView
               // getStudent(selectedStudent.student_id.toString())
            }
        }
    }
//    private fun getStudent(studentId: String) {
//        val studentService = RetrofitInstance.create(StudentService::class.java)
//        studentService.getStudent(studentId).enqueue(object : Callback<Student> {
//            override fun onResponse(
//                call: Call<Student>,
//                response: Response<Student>
//            ) {
//                if (response.isSuccessful) {
//                    val student = response.body()
//                    if (student != null) {
//
//                    }
//                } else {
//                    Log.e("Error", "Failed to fetch student details: ${response.code()}")
//                }
//            }
//
//            override fun onFailure(call: Call<Student>, t: Throwable) {
//                Log.e("Error", "Failed to fetch student details: ${t.message}")
//            }
//        })
//    }

}