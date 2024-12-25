package com.holytrinity.users.student.curr_eval

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.holytrinity.R
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.api.StudentService
import com.holytrinity.databinding.FragmentStudentCurrEvalBinding
import com.holytrinity.model.StudentSolo
import com.holytrinity.util.UserPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StudentCurrEvalFragment : Fragment() {

    private lateinit var binding : FragmentStudentCurrEvalBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStudentCurrEvalBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbarBackButton.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("selectedFragmentId", null ?: R.id.nav_dashboard_student)
            }
            findNavController().navigate(R.id.studentDrawerHolderFragment, bundle)
        }

        val studentID = UserPreferences.getUserId(requireContext())
        getStudent(studentID.toString())
    }

    private fun getStudent(studentId: String) {
        val studentService = RetrofitInstance.create(StudentService::class.java)
        studentService.getStudent(studentId).enqueue(object : Callback<StudentSolo> {
            @SuppressLint("MissingInflatedId")
            override fun onResponse(
                call: Call<StudentSolo>,
                response: Response<StudentSolo>
            ) {
                if (response.isSuccessful) {
                    val student = response.body()
                    if (student != null) {
                        // Show student information in the CardView
//                        binding.studentInfoLayout.visibility = View.VISIBLE
//                        binding.pdfLayout.visibility = View.VISIBLE
//                        binding.studentNameTextView.text = student.student_name
//                        binding.studentNumber.text = "${student.student_id}"

                        // Clear previous subject cards (if any)
                        binding.subjectsContainer.removeAllViews()

                        // Display each enrolled subject in a separate CardView
                        student.enrolled_subjects.forEach { subject ->
                            val subjectCardView = LayoutInflater.from(requireContext()).inflate(R.layout.item_curr_eval, null)

                            // Set subject details in the card view
                            val subjectNameTextView = subjectCardView.findViewById<TextView>(R.id.descriptionTextView)
                            val subjectCodeTextView = subjectCardView.findViewById<TextView>(R.id.codeTextView)
                            val subjectGradeTextView = subjectCardView.findViewById<TextView>(R.id.gradesTextView)
                            val subjectRemarkTextView = subjectCardView.findViewById<TextView>(R.id.remarksTextView)

                            subjectNameTextView.text = subject.subject_name
                            subjectGradeTextView.text = "${subject.grade}"
                            subjectRemarkTextView.text = "${subject.remarks}"

                            subjectCodeTextView.text = "${subject.subject_id}"

                            // Add the subject card to the container
                            binding.subjectsContainer.addView(subjectCardView)
                        }
                    }
                } else {
                    Log.e("Error", "Failed to fetch student details: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<StudentSolo>, t: Throwable) {
                Log.e("Error", "Failed to fetch student details: ${t.message}")
            }
        })
    }
}