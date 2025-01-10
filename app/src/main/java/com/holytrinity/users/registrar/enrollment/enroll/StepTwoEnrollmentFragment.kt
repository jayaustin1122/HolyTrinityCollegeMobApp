package com.holytrinity.users.registrar.enrollment.enroll

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
        val currId = viewModel.curr_id.value
        val level = viewModel.level.value
        val studentID = viewModel.studentID.value!!.toInt()

        val savedPeriod = SharedPrefsUtil.getSelectedPeriod(requireContext())
        val enrollment_period = savedPeriod!!.enrollment_period_id

        getAllSubjects(currId, level, enrollment_period.toString(), studentID)
        Log.d("currid", "$currId $level ")
    }


    private fun getAllSubjects(
        currId: Int?,
        level: String?,
        savedPeriod: String?,
        studentID: Int?
    ) {
        val semester = SharedPrefsUtil.getSelectedPeriod(requireContext())
        if (savedPeriod != null) {
            Log.d(
                "SavedPeriod", "Year: ${semester!!.enrollment_period_id}, Semester: ${semester!!.semester}, " +
                        "Start Date: ${semester!!.start_date}, End Date: ${semester!!.end_date}"
            )
            if (currId == null || level == null || savedPeriod == null || studentID == null) {
                Log.e("API_ERROR", "Invalid parameters: currId=$currId, level=$level, savedPeriod=$savedPeriod, studentID=$studentID")
                return
            }

            val service = RetrofitInstance.create(EnrollmentService::class.java)
            service.enrollFlow(currId, level, savedPeriod.toInt(), studentID,semester.semester)
                .enqueue(object : Callback<EnrollmentResponse> {
                    override fun onResponse(
                        call: Call<EnrollmentResponse>,
                        response: Response<EnrollmentResponse>
                    ) {
                        if (response.isSuccessful) {
                            val enrollmentResponse = response.body()
                            val subjects = enrollmentResponse?.subjects ?: emptyList()
                            val subjectIds = subjects.map { it.subjectId }
                            Log.d("SUBJECT_IDS", "Subject IDs: $subjectIds")
                            viewModel.setSubjects(subjectIds)
                            viewModel.setEnrollmentPeriodId(savedPeriod)
                            setupRecyclerViews(subjects)
                        } else {
                            Log.e("API_ERROR", "Error fetching subjects: ${response.message()}")
                        }
                    }

                    override fun onFailure(call: Call<EnrollmentResponse>, t: Throwable) {
                        Log.e("API_ERROR", "Failed to connect", t)
                    }
                })
        } else {
            Log.d("SavedPeriod", "No saved period found.")
            Toast.makeText(requireContext(), "Please Setup in the Side Nav Setup Config", Toast.LENGTH_SHORT).show()
        }

    }

    private fun setupRecyclerViews(enrollment: List<Subjects>) {
        binding.subjectsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = SubjectEnrollmentAdapter(enrollment)
        binding.subjectsRecyclerView.adapter = adapter
    }
}
