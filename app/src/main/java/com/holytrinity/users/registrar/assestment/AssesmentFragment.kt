package com.holytrinity.users.registrar.assestment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.canorecoapp.utils.DialogUtils
import com.holytrinity.R
import com.holytrinity.api.ApiResponse
import com.holytrinity.api.EnrollmentService
import com.holytrinity.api.FeesService
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.api.StudentService
import com.holytrinity.databinding.FragmentAssesmentBinding
import com.holytrinity.model.EnrollmentResponse
import com.holytrinity.model.Fees
import com.holytrinity.model.Student
import com.holytrinity.model.StudentLedger
import com.holytrinity.users.registrar.adapter.FeesAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AssesmentFragment : Fragment() {
    private lateinit var binding: FragmentAssesmentBinding
    private lateinit var students: List<Student>
    private lateinit var studentNamesMap: MutableMap<String, String>
    private lateinit var feesAdapter: FeesAdapter
    private var totalAmount = 0.0
    private var studentID: String = ""
    private var enrollment_period_id: String = ""
    private var yearLevel: String = ""
    private var curr_id: String = ""
    private var allFeesList: MutableList<Fees> = mutableListOf()
    private var feesFetched = false
    private var enrolledSubsFetched = false
    private lateinit var loadingDialog: SweetAlertDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAssesmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val recyclerView = binding.recyclerView
        feesAdapter = FeesAdapter()

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = feesAdapter

        getAllStudents()
        binding.toolbarBackButton.setOnClickListener {
            DialogUtils.showWarningMessage(
                requireActivity(),
                "Confirm Exit",
                "Click \"Yes\" to cancel discard any changes made."
            ) { sweetAlertDialog ->
                sweetAlertDialog.dismissWithAnimation()
                val bundle = Bundle().apply {
                    putInt("selectedFragmentId", null ?: R.id.nav_dashboard)
                }
                findNavController().navigate(R.id.registrarDrawerHolderFragment, bundle)
            }
        }


            getEnrolledSubs()
            binding.nextButton.setOnClickListener {
                loadingDialog = DialogUtils.showLoading(requireActivity())
                loadingDialog.show()
                addStudentLedgerToApi(
                    studentID.toInt(), enrollment_period_id.toInt(),
                    totalAmount, totalAmount
                )
            }
        fetchFees()


    }

    private fun addStudentLedgerToApi(
        studentId: Int,
        enrollmentPeriodId: Int,
        totalDue: Double,
        balance: Double
    ) {
        val studentLedger = RetrofitInstance.create(StudentService::class.java)

        studentLedger.addStudentLedger(
            studentId,
            enrollmentPeriodId,
            totalDue,
            balance
        ).enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    // Handle success
                    Log.d("Retrofit", "Student Ledger added successfully")
                    loadingDialog.dismiss()
                    DialogUtils.showSuccessMessage(requireActivity(),"Success","Student Assessment Complete")
                    findNavController().navigate(R.id.registrarDrawerHolderFragment)
                } else {
                    loadingDialog.dismiss()
                    Log.e("Retrofit", "Failed to add student ledger: ${response.errorBody()}")
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                loadingDialog.dismiss()
                Log.e("Retrofit", "Error: ${t.message}")
            }
        })
    }


    // Fetch enrolled subjects and add them to the container (allFeesList)
    private fun getEnrolledSubs() {
        val service = RetrofitInstance.create(EnrollmentService::class.java)

        if (yearLevel.isBlank()) {
            Log.e("getEnrolledSubs", "Year level is empty or invalid")
            return
        }

        val yearLevelString = getYearLevelString(yearLevel)
        Log.d(
            "getEnrolledSubs",
            "Year Level: $yearLevelString, curr_id: $curr_id, enrollment_period_id: $enrollment_period_id, studentID: $studentID"
        )

        service.enrollFlow(
            curr_id.toInt(),
            yearLevelString,
            enrollment_period_id.toInt(),
            studentID.toInt()
        )
            .enqueue(object : Callback<EnrollmentResponse> {
                override fun onResponse(
                    call: Call<EnrollmentResponse>,
                    response: Response<EnrollmentResponse>
                ) {
                    if (response.isSuccessful) {
                        val enrollmentResponse = response.body()
                        if (enrollmentResponse != null) {
                            Log.d("getEnrolledSubs", "Enrollment Response: $enrollmentResponse")

                            val subjects = enrollmentResponse.subjects ?: emptyList()
                            val enrolledFeesList = subjects.map { subject ->
                                val title = "${subject.name} (${subject.units} units)"
                                val amount = subject.units * 250.0 // Example rate per unit

                                Fees(
                                    fee_id = null,
                                    title = title,
                                    amount = amount,
                                    description = title,
                                    data = null
                                )
                            }

                            // Add enrolled subject fees to the container
                            allFeesList.addAll(enrolledFeesList)
                            enrolledSubsFetched = true
                            checkAndUpdateAdapter()
                        } else {
                            Log.e("getEnrolledSubs", "Enrollment Response is null")
                        }
                    } else {
                        Log.e(
                            "API_ERROR",
                            "Error fetching subjects: ${response.message()} | Code: ${response.code()}"
                        )
                    }
                }

                override fun onFailure(call: Call<EnrollmentResponse>, t: Throwable) {
                    Log.e("API_ERROR", "Failed to connect", t)
                }
            })
    }

    // Fetch the student fees and add them to the container (allFeesList)
    private fun fetchFees() {
        val feesService = RetrofitInstance.create(FeesService::class.java)
        feesService.getStudentFees().enqueue(object : Callback<Fees> {
            override fun onResponse(call: Call<Fees>, response: Response<Fees>) {
                if (response.isSuccessful && response.body() != null) {
                    val fees = response.body()!!.data ?: emptyList()

                    // Add student fees to the container
                    allFeesList.addAll(fees)
                    feesFetched = true
                    checkAndUpdateAdapter()
                } else {
                    Toast.makeText(
                        this@AssesmentFragment.requireContext(),
                        "Failed to fetch fees",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Fees>, t: Throwable) {
                Toast.makeText(
                    this@AssesmentFragment.requireContext(),
                    "Error: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun checkAndUpdateAdapter() {
        if (feesFetched && enrolledSubsFetched) {
            feesAdapter.submitList(allFeesList)
            calculateTotalAmount(allFeesList)
        }
    }

    private fun calculateTotalAmount(fees: List<Fees>) {
        totalAmount = fees.sumOf { it.amount }
        binding.totalAmountTextView.text = totalAmount.toString()

    }

    // Return year level as string based on the integer level
    private fun getYearLevelString(yearLevel: String): String {
        return when (yearLevel.toIntOrNull()) {
            1 -> "1st Year"
            2 -> "2nd Year"
            3 -> "3rd Year"
            4 -> "4th Year"
            else -> "Unknown Year"
        }
    }

    private fun getAllStudents(studentId: Int? = null, registrationVerified: Int? = 1) {
        val studentService = RetrofitInstance.create(StudentService::class.java)
        studentService.getStudents(studentId, registrationVerified).enqueue(object :
            Callback<List<Student>> {
            override fun onResponse(
                call: Call<List<Student>>,
                response: Response<List<Student>>
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
                    studentNamesMap =
                        students.associate { it.student_name!! to it.student_id.toString() }
                            .toMutableMap()

                    setupAutoCompleteTextView()
                } else {
                    Log.e("Error", "Failed to fetch students: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Student>>, t: Throwable) {
                Log.e("Error", "Failed to fetch students: ${t.message}")
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
            val selectedStudent = students.find { it.student_id.toString() == selectedStudentID }

            if (selectedStudent != null) {
                studentID = selectedStudent.student_id.toString()
                Log.d("StudentData", "Assigned studentID: $studentID")

                enrollment_period_id = selectedStudent.entry_period_id.toString()
                Log.d("StudentData", "Assigned enrollment_period_id: $enrollment_period_id")

                yearLevel = selectedStudent.level.toString()
                Log.d("StudentData", "Assigned yearLevel: $yearLevel")

                curr_id = selectedStudent.curriculum_id.toString()
                Log.d("StudentData", "Assigned curr_id: $curr_id")
                binding.studentNumber.text = selectedStudent.student_id.toString()
                binding.studentName.text = selectedStudent.student_name.toString()
                binding.assessmentCardView.visibility = View.VISIBLE
                binding.nextButton.visibility = View.VISIBLE
                getEnrolledSubs()

            } else {
                Toast.makeText(requireContext(), "Student not found", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
