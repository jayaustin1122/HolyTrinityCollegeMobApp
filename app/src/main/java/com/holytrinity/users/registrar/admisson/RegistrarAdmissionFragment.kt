package com.holytrinity.users.registrar.admisson


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.holytrinity.R
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.api.StudentService
import com.holytrinity.databinding.FragmentRegistrarAdmissionBinding
import com.holytrinity.model.Student
import com.holytrinity.users.registrar.adapter.RegistrarAdmissionAdapter
import com.holytrinity.users.registrar.adapter.SoaAdapter
import com.holytrinity.users.registrar.enrollment.enroll.ViewModelEnrollment
import retrofit2.Call
import retrofit2.Callback

class RegistrarAdmissionFragment : Fragment() {

    private lateinit var binding: FragmentRegistrarAdmissionBinding
    private var filteredStudents: List<Student> = emptyList()
    private lateinit var studentsAdapter: RegistrarAdmissionAdapter
    private var students: List<Student> = emptyList()
    private lateinit var studentNamesMap: MutableMap<String, String>
    private lateinit var viewModel: ViewModelEnrollment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegistrarAdmissionBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[ViewModelEnrollment::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        studentsAdapter = RegistrarAdmissionAdapter(filteredStudents) { studentId ->
            val bundle = Bundle().apply {
                putString("studentId", studentId)
            }
            findNavController().navigate(R.id.registrarAdmissionDetailsFragment, bundle)
        }


        binding.toolbarBackButton.setOnClickListener {
            findNavController().navigate(R.id.registrarDrawerHolderFragment)
        }




        binding.recyclerEnrolledSubjects.layoutManager = LinearLayoutManager(context)
        binding.recyclerEnrolledSubjects.adapter = studentsAdapter
        binding.useridTextView.addTextChangedListener {
            val query = it.toString().trim()
            filterStudents(query)
        }
        getAllStudents()
    }


    private fun getAllStudents(studentId: String? = null, registrationVerified: String? = null) {
        val studentService = RetrofitInstance.create(StudentService::class.java)
        studentService.getStudents(studentId, registrationVerified).enqueue(object :
            Callback<List<Student>> {
            override fun onResponse(
                call: Call<List<Student>>,
                response: retrofit2.Response<List<Student>>
            ) {
                if (response.isSuccessful) {
                    students = response.body() ?: emptyList()
                    filteredStudents = students
                    // Ensure that we are returning a Pair<String, String> for the map
                    studentNamesMap = students.associate {
                        // Safely convert to String and provide default value if null
                        val fullName = it.student_name?.toString() ?: "Unknown"
                        val studentId = it.student_id?.toString() ?: "Unknown"
                        fullName to studentId  // Returning the pair
                    }.toMutableMap()


                    studentsAdapter = RegistrarAdmissionAdapter(filteredStudents) { studentId ->

                        val bundle = Bundle().apply {
                            putString("studentId", studentId)
                        }

                        findNavController().navigate(R.id.registrarAdmissionDetailsFragment, bundle)
                    }


                    binding.recyclerEnrolledSubjects.adapter = studentsAdapter
                    studentsAdapter.notifyDataSetChanged()

                } else {
                    Log.e("Error", "Failed to fetch students: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Student>>, t: Throwable) {
                Log.e("Error", "Failed to fetch students: ${t.message}")
            }
        })
    }
    private fun filterStudents(query: String) {
        filteredStudents = if (query.isEmpty()) {
            students
        } else {
            students.filter { it.student_name?.contains(query, ignoreCase = true) == true }
        }
        studentsAdapter.updateData(filteredStudents)  // Update the adapter with filtered data
    }

}