package com.holytrinity.users.registrar.enrollment.enroll

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.canorecoapp.utils.DialogUtils
import com.google.android.material.tabs.TabLayoutMediator
import com.holytrinity.R
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.api.StudentService
import com.holytrinity.databinding.FragmentRegistrarEnrollmentBinding
import com.holytrinity.model.Soa
import com.holytrinity.model.Student
import com.holytrinity.users.registrar.adapter.EnrollmentAdapter
import com.holytrinity.users.registrar.adapter.SoaAdapter
import retrofit2.Call
import retrofit2.Callback


class RegistrarEnrollmentFragment : Fragment() {
    private lateinit var binding : FragmentRegistrarEnrollmentBinding
    private lateinit var soaList: List<Soa>
    private lateinit var loadingDialog: SweetAlertDialog
    private lateinit var soaAdapter: SoaAdapter
    private lateinit var students: List<Student>
    private lateinit var studentNamesMap: MutableMap<String, String>
    private lateinit var studentNames: MutableMap<String, String>
    private lateinit var viewModel: ViewModelEnrollment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegistrarEnrollmentBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[ViewModelEnrollment::class.java]
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentList = arrayListOf<Fragment>(
            StepOneEnrollmentFragment(),
            StepTwoEnrollmentFragment(),
            StepThreeEnrollmentFragment()
        )

        val adapter = EnrollmentAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        binding.viewPager.adapter = adapter

        // Link TabLayout with ViewPager2 for dots indicator
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { _, _ -> }.attach()

        binding.backButton.setOnClickListener {
            val currentItem = binding.viewPager.currentItem
            if (currentItem < fragmentList.size - 1) {
                binding.viewPager.currentItem = currentItem - 1
            } else {
                // If on the last page, you can navigate to another screen
                findNavController().navigate(R.id.registrarDrawerHolderFragment)
            }
        }

        // Handle Next button click
        binding.nextButton.setOnClickListener {
            val currentItem = binding.viewPager.currentItem
            if (currentItem < fragmentList.size - 1) {
                binding.viewPager.currentItem = currentItem + 1
            } else {
                // If on the last page, you can navigate to another screen
                findNavController().navigate(R.id.registrarDrawerHolderFragment)
            }
        }
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
                        Log.d("StudentData", "ID: ${student.studentID}, Name: ${student.name}, Email: ${student.email}, Phone: ${student.phone}")
                    }
                    studentNamesMap = students.associate { it.name to it.studentID }.toMutableMap()
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
        binding.useridTextView.setAdapter(adapter)

        binding.useridTextView.setOnItemClickListener { parent, _, position, _ ->
            val selectedName = parent.getItemAtPosition(position) as String
            val selectedStudentID = studentNamesMap[selectedName]
            val selectedStudent = students.find { it.studentID == selectedStudentID }

            if (selectedStudent != null) {
                viewModel.apply {
                    studentID = selectedStudent.studentID ?: ""
                    name = selectedStudent.name ?: ""
                    email = selectedStudent.email ?: ""
                    phone = selectedStudent.phone ?: ""
                    curr_id = selectedStudent.curr_id ?: ""
                    department_id = selectedStudent.department_id ?: ""
                    course = selectedStudent.course ?: ""
                    level = selectedStudent.level ?: ""
                    status = selectedStudent.status ?: ""
                    section = selectedStudent.section ?: ""
                    classification_of_student = selectedStudent.classification_of_student ?: ""
                    birthdate = selectedStudent.birthdate ?: ""
                    sex = selectedStudent.sex ?: ""
                    address = selectedStudent.address ?: ""

                }

                binding.viewPager.visibility = View.VISIBLE
            } else {
                Toast.makeText(requireContext(), "Student not found", Toast.LENGTH_SHORT).show()
            }
        }
    }


}
