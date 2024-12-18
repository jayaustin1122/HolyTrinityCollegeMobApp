package com.holytrinity.users.registrar.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.holytrinity.databinding.ItemSubjectBinding
import com.holytrinity.model.SubjectsModel

class SubjectsAdapter(private val subjects: List<SubjectsModel>) :
    RecyclerView.Adapter<SubjectsAdapter.SubjectViewHolder>() {

    // Create ViewHolder to hold the binding reference
    inner class SubjectViewHolder(private val binding: ItemSubjectBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // Bind the subject data to the UI
        fun bind(subject: SubjectsModel) {
            binding.subjectName.text = subject.name
            binding.subjectCode.text = subject.code
            binding.subjectDescription.text = subject.description
            binding.subjectSchedule.text = subject.schedule
            binding.subjectUnits.text = "Units: ${subject.units}"
            binding.subjectType.text = "Type: ${subject.type}"
            binding.subjectYear.text = "Year: ${subject.year}"
        }
    }

    // Create new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        // Inflate the binding instead of the layout directly
        val binding = ItemSubjectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SubjectViewHolder(binding)
    }

    // Bind the data to the ViewHolder
    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        val subject = subjects[position]
        holder.bind(subject)
    }

    // Return the number of items in the list
    override fun getItemCount(): Int = subjects.size
}
