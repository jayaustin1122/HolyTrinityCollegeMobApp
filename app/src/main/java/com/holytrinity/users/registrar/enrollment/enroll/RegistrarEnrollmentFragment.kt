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
import com.holytrinity.api.PreEnlistService
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.api.StudentService
import com.holytrinity.databinding.FragmentRegistrarEnrollmentBinding
import com.holytrinity.model.PreEnlistRequest
import com.holytrinity.model.Soa
import com.holytrinity.model.Student
import com.holytrinity.users.registrar.adapter.EnrollmentAdapter
import com.holytrinity.users.registrar.adapter.SoaAdapter
import com.holytrinity.util.SharedPrefsUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class RegistrarEnrollmentFragment : Fragment() {
    private lateinit var binding: FragmentRegistrarEnrollmentBinding
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
            StepTwoEnrollmentFragment()
        )

        val adapter = EnrollmentAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        binding.viewPager.adapter = adapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { _, _ -> }.attach()

        binding.backButton.setOnClickListener {
            val currentItem = binding.viewPager.currentItem
            if (currentItem < fragmentList.size - 1) {
                binding.viewPager.currentItem = currentItem - 1
            } else {
                findNavController().navigate(R.id.registrarDrawerHolderFragment)
            }
        }

        binding.nextButton.setOnClickListener {
            val currentItem = binding.viewPager.currentItem
            if (currentItem < fragmentList.size - 1) {
                binding.viewPager.currentItem = currentItem + 1
            } else {
                val currentLevel = viewModel.level.value
                Log.d("Level", "Current level: $currentLevel")
                DialogUtils.showWarningMessage(
                    requireActivity(),
                    "Are you sure?",
                    "You want to Add this Student in the Pre Enlisted Students?",
                    confirmListener = SweetAlertDialog.OnSweetClickListener {
                        saveStudentInPreEnlist()
                        it.dismissWithAnimation()
                    }
                )

//                DialogUtils.showSuccessMessage(requireActivity(),"Success","Student Added in ")
                findNavController().navigate(R.id.registrarDrawerHolderFragment)
            }
        }
        studentNames = mutableMapOf()
        loadingDialog = DialogUtils.showLoading(requireActivity())
        loadingDialog.show()

        getAllStudents()

        binding.toolbarBackButton.setOnClickListener {
            DialogUtils.showWarningMessage(requireActivity(), "Confirm Exit", "Click \"Yes\" to cancel discard any changes made."
            ) { sweetAlertDialog ->
                sweetAlertDialog.dismissWithAnimation()

                val bundle = Bundle().apply {
                    putInt("selectedFragmentId", null ?: R.id.nav_dashboard)
                }
                findNavController().navigate(R.id.registrarDrawerHolderFragment, bundle)
            }
        }
    }
    private fun saveStudentInPreEnlist() {
        val deptId = viewModel.dept_id.value?.toString() ?: ""
        val course = viewModel.course.value?.toString() ?: ""
        val currId = viewModel.curr_id.value?.toString() ?: ""
        val studentID = viewModel.studentID.value?.toString() ?: return
        val enrollmentPeriodID = viewModel.enrollment_period_id.value?.toString() ?: return
        val subjectsIds = viewModel.subject.value ?: return
        val service = RetrofitInstance.create(PreEnlistService::class.java)
        val currentLevel = viewModel.level.value
        Log.d("Level", "Current level: $currentLevel")
        subjectsIds.forEach { subjectId ->
            service.insertPreEnlist(
                studentId = studentID,
                subjectId = subjectId.toString(),
                enrollmentPeriodId = enrollmentPeriodID,
                deptId = deptId,
                courseId = course,
                curriculumId = currId,
                level = currentLevel!!
            ).enqueue(object : Callback<PreEnlistRequest> {

                override fun onResponse(call: Call<PreEnlistRequest>, response: Response<PreEnlistRequest>) {
                    if (response.isSuccessful) {
                        Log.d("Registrar", "Subject $subjectId pre-enlisted successfully.")
                    } else {
                        Log.e(
                            "Registrar",
                            "Failed to pre-enlist subject $subjectId: ${response.errorBody()?.string()}"
                        )
                    }
                }

                override fun onFailure(call: Call<PreEnlistRequest>, t: Throwable) {
                    Log.e("Registrar", "Error inserting subject $subjectId: ${t.message}")
                }
            })
        }
    }



    private fun getAllStudents(studentId: Int? = null, registrationVerified: Int? = 1) {
        val studentService = RetrofitInstance.create(StudentService::class.java)
        studentService.getStudents(studentId, registrationVerified).enqueue(object : Callback<List<Student>> {
            override fun onResponse(
                call: Call<List<Student>>,
                response: retrofit2.Response<List<Student>>
            ) {
                if (response.isSuccessful) {
                    students = response.body() ?: emptyList()
                    students.forEach { student ->
                        Log.d(
                            "StudentData",
                            "ID: ${student.student_id}, Name: ${student.student_name}, Dept: ${student.dept_id}"
                        )

                    }
                    // Correctly associate student name with student_id
                    studentNamesMap = students.associate { it.student_name!! to it.student_id.toString() }.toMutableMap()

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
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            studentNamesList
        )
        binding.useridTextView.setAdapter(adapter)

        binding.useridTextView.setOnItemClickListener { parent, _, position, _ ->
            val selectedName = parent.getItemAtPosition(position) as String
            val selectedStudentID = studentNamesMap[selectedName]

            // Find the student using the selected ID
            val selectedStudent = students.find { it.student_id.toString() == selectedStudentID }

            if (selectedStudent != null) {
                viewModel.setStudentID((selectedStudent.student_id ?: "").toString())
                viewModel.setName(selectedStudent.student_name ?: "")
                viewModel.setDeptId((selectedStudent.dept_id ?: "").toString())

                Log.d("ViewModelAssignment", "studentID: ${selectedStudent.student_id}")
                Log.d("ViewModelAssignment", "name: ${selectedStudent.student_name}")
                Log.d("ViewModelAssignment", "dept_id: ${selectedStudent.dept_id}")

                binding.viewPager.visibility = View.VISIBLE
            } else {
                Toast.makeText(requireContext(), "Student not found", Toast.LENGTH_SHORT).show()
            }
        }
    }



}
