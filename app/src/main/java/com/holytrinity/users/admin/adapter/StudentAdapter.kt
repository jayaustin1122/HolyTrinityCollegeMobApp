package com.holytrinity.users.admin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.holytrinity.R
import com.holytrinity.model.Student

class StudentAdapter(private val studentList: List<Student>) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_parent_benefactor, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = studentList[position]
        holder.tvNo.text = (position + 1).toString() // Display row number
        holder.tvIdNo.text = student.student_id.toString() // Display student ID
        holder.tvName.text = student.student_name // Display student name
    }

    override fun getItemCount(): Int {
        return studentList.size
    }

    class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNo: TextView = itemView.findViewById(R.id.tvNo)
        val tvIdNo: TextView = itemView.findViewById(R.id.tvIdNo)
        val tvName: TextView = itemView.findViewById(R.id.tvName)
    }
}
