package com.holytrinity.users.registrar.adapter


import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.holytrinity.R
import com.holytrinity.databinding.ItemAdmissionBinding
import com.holytrinity.model.Student

class RegistrarAdmissionAdapter(
    private var students: List<Student>,
    private val onClick: (String) -> Unit
) : RecyclerView.Adapter<RegistrarAdmissionAdapter.StudentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val binding = ItemAdmissionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StudentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = students[position]
        holder.bind(student)
    }

    override fun getItemCount(): Int = students.size

    fun updateData(newStudents: List<Student>) {
        students = newStudents
        notifyDataSetChanged()
    }

    inner class StudentViewHolder(private val binding: ItemAdmissionBinding) : RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("ResourceType")
        fun bind(student: Student) {
            // Set basic details
            binding.admissionTextView.text = "Admission No: ${student.student_id}"
            binding.dateTextView.text = "Date: ${student.entry_date}"
            binding.nameTextView.text = student.student_name
            binding.departmentTextView.text = getDepartmentName(student.dept_id.toString())

            // Set status text and background
            when (student.registration_verified) {
                "0" -> {
                    binding.tvStatus.text = "Pending"
                    binding.tvStatus.setBackgroundResource(R.drawable.background_pending) // Use your drawable for "Pending"
                    binding.tvStatus.setTextColor(Color.YELLOW)
                }

                "1" -> {
                    binding.tvStatus.text = "Admitted"
                    binding.tvStatus.setBackgroundResource(R.drawable.background_admitted) // Use your drawable for "Admitted"
                    binding.tvStatus.setTextColor(Color.GREEN)
                }

                "2" -> {
                    binding.tvStatus.text = "Denied"
                    binding.tvStatus.setBackgroundResource(R.drawable.background_denied) // Use your drawable for "Denied"
                    binding.tvStatus.setTextColor(Color.RED)
                }

                else -> {
                    binding.tvStatus.text = "Unknown"
                    binding.tvStatus.setBackgroundResource(R.drawable.background_pending) // Optional drawable for unknown
                    binding.tvStatus.setTextColor(Color.GRAY)
                }
            }

            // Set onClickListener
            itemView.setOnClickListener {
                onClick(student.student_id.toString())
            }
        }
        private fun getDepartmentName(deptId: String): String {
            return when (deptId) {
                "1" -> "College"
                "2" -> "Pre College"
                "3" -> "Senior High"
                else -> "Unknown"
            }
        }
    }
}
