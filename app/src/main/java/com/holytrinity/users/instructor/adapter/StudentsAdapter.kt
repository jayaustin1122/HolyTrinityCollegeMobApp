package com.holytrinity.users.instructor.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.holytrinity.databinding.ItemClassStudentBinding
import com.holytrinity.model.StudentItem

class StudentsAdapter(
    private var studentsList: List<StudentItem>,
    private val classId: Int, // Pass the classId here
    private val onItemClick: (studentId: Int, classId: Int) -> Unit // Callback for item click
) : RecyclerView.Adapter<StudentsAdapter.StudentViewHolder>() {

    inner class StudentViewHolder(private val binding: ItemClassStudentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(studentItem: StudentItem) {
            binding.studentIDTextView.text = studentItem.student_id.toString()
            binding.studentNameTextView.text = studentItem.student_name
            binding.gradesTextView.text = studentItem.grade
            binding.remarksTextView.text = studentItem.remark
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val binding = ItemClassStudentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StudentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = studentsList[position]
        holder.bind(student)

        // Handle item click and pass studentId and classId
        holder.itemView.setOnClickListener {
            onItemClick(student.student_id, classId)
        }
    }

    override fun getItemCount(): Int = studentsList.size

    fun updateData(newStudentsList: List<StudentItem>) {
        studentsList = newStudentsList
        notifyDataSetChanged()
    }
}
