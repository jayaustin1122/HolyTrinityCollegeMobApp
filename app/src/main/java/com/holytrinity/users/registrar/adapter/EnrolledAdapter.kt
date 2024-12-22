package com.holytrinity.users.registrar.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.api.StudentService
import com.holytrinity.databinding.DialogStudentDetailsBinding
import com.holytrinity.databinding.ItemEnrollementListsBinding
import com.holytrinity.model.Student
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.app.AlertDialog
import android.content.Context
import com.holytrinity.model.StudentSolo
import com.holytrinity.users.registrar.registrar.RegistrarEnrollmentListFragment
import com.holytrinity.users.registrar.registrar.RegistrarHolderFragment

class EnrolledAdapter(
    private var students: List<Student>,
    private val context: Context,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<EnrolledAdapter.StudentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val binding = ItemEnrollementListsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StudentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = students[position]
        holder.bind(student)
    }

    override fun getItemCount(): Int = students.size

    fun updateData(newStudents: List<Student>) {
        students = newStudents.filter { it.official_status == "Official" }
        notifyDataSetChanged()
    }

    inner class StudentViewHolder(private val binding: ItemEnrollementListsBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("ResourceType")
        fun bind(student: Student) {
            // Set basic details
            binding.tvNo.text = (adapterPosition + 1).toString()
            binding.tvIdNo.text = student.student_id.toString()
            binding.tvName.text = student.student_name

            // Handle click listener to show student details
            binding.root.setOnClickListener {
                getStudent(student.student_id.toString())
            }
        }
    }

    private fun getStudent(studentId: String) {
        val studentService = RetrofitInstance.create(StudentService::class.java)
        studentService.getStudent(studentId).enqueue(object : Callback<StudentSolo> { // Change to Student (single object)
            override fun onResponse(
                call: Call<StudentSolo>,
                response: Response<StudentSolo>
            ) {
                if (response.isSuccessful) {
                    val student = response.body() // Single student object
                    if (student != null) {
                        showStudentDetailsDialog(student)
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
    @SuppressLint("SetTextI18n")
    private fun showStudentDetailsDialog(student: StudentSolo) {
        val dialogBinding = DialogStudentDetailsBinding.inflate(LayoutInflater.from(context))

        dialogBinding.tvStudentName.text = "Name: ${student.student_name ?: "No Data"}"
        dialogBinding.tvStudentId.text = "Student ID: ${student.student_id ?: "No Data"}"
        dialogBinding.tvGender.text = "Gender: ${student.gender ?: "No Data"}"
        dialogBinding.tvBirth.text = "Birthdate: ${student.birthdate ?: "No Data"}"
        dialogBinding.tvCourse.text = "Course: ${student.course.course_name ?: "No Data"}"
        dialogBinding.tvLevel.text = "Level: ${student.course.course_code ?: "No Data"}"
        dialogBinding.tvContact.text = if (student.contacts.isNotEmpty()) {
            "Contact: ${student.contacts.first().contact_value}"
        } else {
            "No contact data available"
        }

        val dialog = AlertDialog.Builder(context)
            .setView(dialogBinding.root)
            .create()


        dialogBinding.llSubjectDetails.removeAllViews()

        if (student.enrolled_subjects.isNotEmpty()) {
            student.enrolled_subjects.forEach { enrolledSubject ->
                val subjectView = TextView(dialogBinding.root.context).apply {
                    text = "Subject: ${enrolledSubject.subject_name}, Units: ${enrolledSubject.subject_units}"
                    setPadding(0, 0, 0, 8)
                }
                dialogBinding.llSubjectDetails.addView(subjectView)

                val classInfo = enrolledSubject.class_info
                if (classInfo != null) {
                    val classDetailsView = TextView(dialogBinding.root.context).apply {
                        text = "Schedule: ${classInfo.schedule}, Section: ${classInfo.section}, Instructor: ${classInfo.instructor.instructor_name}"
                        setPadding(0, 0, 0, 8)
                    }
                    dialogBinding.llSubjectDetails.addView(classDetailsView)
                }
            }
        } else {
            val noSubjectsView = TextView(dialogBinding.root.context).apply {
                text = "No enrolled subjects available."
                setPadding(0, 8, 0, 8)
            }
            dialogBinding.llSubjectDetails.addView(noSubjectsView)
        }

        dialog.show()
    }

}
