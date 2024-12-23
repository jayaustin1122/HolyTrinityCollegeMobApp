package com.holytrinity.users.registrar.enrollment.enroll

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.holytrinity.api.EnrollmentService
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.databinding.FragmentStepTwoEnrollmentBinding
import com.holytrinity.model.EnrollmentResponse
import com.holytrinity.model.Subjects
import com.holytrinity.users.registrar.adapter.SubjectEnrollmentAdapter
import com.holytrinity.util.SharedPrefsUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class StepTwoEnrollmentFragment : Fragment() {
    private lateinit var binding: FragmentStepTwoEnrollmentBinding
    private lateinit var viewModel: ViewModelEnrollment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStepTwoEnrollmentBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[ViewModelEnrollment::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val studentId = viewModel.studentID.value
        val name = viewModel.name.value
        val deptId = viewModel.dept_id.value // dept id
        val currId = viewModel.curr_id.value!!.toInt() // curriculum id
        val course = viewModel.course.value //course id
        val status = viewModel.status.value
        val section = viewModel.section.value
        val classification = viewModel.classification_of_student.value
        val level = viewModel.level.value // year level 1st Year
        val studentID = viewModel.studentID.value!!.toInt()  // student_id

        val savedPeriod = SharedPrefsUtil.getSelectedPeriod(requireContext())
        val enrollment_period = savedPeriod!!.enrollment_period_id

        getAllSubjects(currId, level, enrollment_period.toString(), studentID)
    }


    private fun getAllSubjects(
        currId: Int?,
        level: String?,
        savedPeriod: String?,
        studentID: Int?
    ) {
        // Ensure that parameters are not null before proceeding
        if (currId == null || level == null || savedPeriod == null || studentID == null) {
            Log.e("API_ERROR", "Invalid parameters: currId=$currId, level=$level, savedPeriod=$savedPeriod, studentID=$studentID")
            return
        }

        // Create the Retrofit instance
        val service = RetrofitInstance.create(EnrollmentService::class.java)
        service.enrollFlow(currId, level, savedPeriod.toInt(), studentID)
            .enqueue(object : Callback<EnrollmentResponse> { // Handle the new response type
                override fun onResponse(
                    call: Call<EnrollmentResponse>,
                    response: Response<EnrollmentResponse>
                ) {
                    if (response.isSuccessful) {
                        val enrollmentResponse = response.body() // Get the EnrollmentResponse
                        val subjects = enrollmentResponse?.subjects ?: emptyList() // Access the subjects list

                        // Store subject IDs and log them
                        val subjectIds = subjects.map { it.subjectId } // Extract subject IDs from response
                        Log.d("SUBJECT_IDS", "Subject IDs: $subjectIds")

                        // Store subject IDs in ViewModel or SharedPrefs if needed
                        viewModel.setSubjects(subjectIds)  // Assuming you add a method in ViewModel to set this
                        viewModel.setEnrollmentPeriodId(savedPeriod)
                        setupRecyclerViews(subjects) // Pass the subjects list to your RecyclerView
                    } else {
                        Log.e("API_ERROR", "Error fetching subjects: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<EnrollmentResponse>, t: Throwable) {
                    Log.e("API_ERROR", "Failed to connect", t)
                }
            })
    }

    private fun setupRecyclerViews(enrollment: List<Subjects>) {
        binding.subjectsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = SubjectEnrollmentAdapter(enrollment) // Pass the Enrollment object
        binding.subjectsRecyclerView.adapter = adapter
    }
}
