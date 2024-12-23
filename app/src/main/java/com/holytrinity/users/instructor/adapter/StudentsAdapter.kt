package com.holytrinity.users.instructor.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.holytrinity.databinding.ItemClassStudentBinding
import com.holytrinity.model.StudentItem

class StudentsAdapter(
    private var studentsList: List<StudentItem>
) : RecyclerView.Adapter<StudentsAdapter.StudentViewHolder>() {

    // ViewHolder class using View Binding
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
        holder.bind(studentsList[position])
    }

    override fun getItemCount(): Int = studentsList.size

    // Method to update the data
    fun updateData(newStudentsList: List<StudentItem>) {
        studentsList = newStudentsList
        notifyDataSetChanged()
    }
}