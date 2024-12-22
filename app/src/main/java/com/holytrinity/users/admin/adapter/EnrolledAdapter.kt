package com.holytrinity.users.admin.adapter


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.holytrinity.databinding.DialogStudentDetailsBinding
import com.holytrinity.databinding.ItemEnrollementListsBinding
import com.holytrinity.model.Student


class EnrolledAdapter(
    private var students: List<Student>,
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
             //   showStudentDetailsDialog(student)
            }
        }
//        @SuppressLint("SetTextI18n")
//        private fun showStudentDetailsDialog(student: Student) {
//            // Inflate dialog layout using View Binding
//            val dialogBinding = DialogStudentDetailsBinding.inflate(LayoutInflater.from(binding.root.context))
//
//            // Set student details in the dialog
//            dialogBinding.tvStudentName.text = "Name: ${student.student_name}"
//            dialogBinding.tvStudentId.text = "Student ID: ${student.student_id}"
//            dialogBinding.tvGender.text = "Gender: ${student.gender ?: "No Data"}"
//            dialogBinding.tvContact.text = "Contact No: ${student.contacts.firstOrNull()?.contact_value ?: "No Data"}"
//            dialogBinding.tvBirth.text = "Birthdate: ${student.dateOfBirth ?: "No Data"}"
//            dialogBinding.tvCourse.text = "Course: ${student.course.course_name}"
//            dialogBinding.tvLevel.text = "Level: ${student.level}"
//
//            // Display enrolled subjects (with units)
//            dialogBinding.llSubjectDetails.removeAllViews()
//            student.enrolled_subjects.forEach { subject ->
//                val subjectView = TextView(binding.root.context).apply {
//                    text = "Code: ${subject.subject?.subject_code}, " +
//                            "Name: ${subject.subject?.subject_name}, " +
//                            "Units: ${subject.subject?.subject_units}, " +
//
//                    setPadding(0, 8, 0, 8)
//                }
//                dialogBinding.llSubjectDetails.addView(subjectView)
//            }
//
//            // Display addresses
//            dialogBinding.llAddressDetails.removeAllViews()
//            student.addresses.forEach { address ->
//                val addressView = TextView(binding.root.context).apply {
//                    text = "Address: ${address.line1} ${address.line2}"
//                    setPadding(0, 8, 0, 8)
//                }
//                dialogBinding.llAddressDetails.addView(addressView)
//            }
//
//            // Show dialog
//            val dialog = android.app.AlertDialog.Builder(binding.root.context)
//                .setView(dialogBinding.root)
//                .create()
//
//            dialog.show()
//        }
    }
}
