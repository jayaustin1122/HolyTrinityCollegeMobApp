package com.holytrinity.users.parent.child


import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.holytrinity.R
import com.holytrinity.databinding.ItemAdmissionBinding
import com.holytrinity.model.StudentSolo

class StudentAdapter(val students: MutableList<StudentSolo>) :
    RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val binding = ItemAdmissionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StudentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = students[position]
        holder.bind(student)
    }

    override fun getItemCount(): Int = students.size

    inner class StudentViewHolder(private val binding: ItemAdmissionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(student: StudentSolo) {
            // Log the student data to ensure it's correctly passed to the ViewHolder
            Log.d("StudentAdapter", "Binding student: ${student.student_name}, ID: ${student.student_id}, Entry Date: ${student.official_status}")

            // Set admission details
            binding.admissionTextView.text = "Admission No: ${student.student_id}"
            binding.dateTextView.text = "Date: ${student.entry_date}"
            binding.nameTextView.text = student.student_name
            binding.departmentTextView.text = student.dept_id?.let { getDepartmentName(student.dept_id.toString()) } ?: "N/A"

            // Set status text and background based on registration_verified value
            when (student.official_status) {
                "Official" -> {
                    binding.tvStatus.text = "Enrrolled"
                    binding.tvStatus.setBackgroundResource(R.drawable.background_pending)
                    binding.tvStatus.setTextColor(Color.YELLOW)
                }

                "Unofficial" -> {
                    binding.tvStatus.text = "Not Enrolled"
                    binding.tvStatus.setBackgroundResource(R.drawable.background_denied)
                    binding.tvStatus.setTextColor(Color.RED)
                }
            }
        }
    }


    private fun getDepartmentName(deptId: String): String {
        val departmentMap = mapOf(
            "1" to "College",
            "2" to "Pre College",
            "3" to "Senior High"
        )

        // Return the department name or "Unknown" if dept_id is not found
        return departmentMap[deptId] ?: "Unknown"
    }

}
