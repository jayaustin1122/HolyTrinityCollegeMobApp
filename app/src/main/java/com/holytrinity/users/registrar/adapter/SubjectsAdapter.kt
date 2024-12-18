package com.holytrinity.users.registrar.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.holytrinity.databinding.ItemPreEnlistedSubjectBinding
import com.holytrinity.model.SubjectsModel

class SubjectsAdapter(private val subjects: List<SubjectsModel>) :
    RecyclerView.Adapter<SubjectsAdapter.SubjectViewHolder>() {

    // Create ViewHolder to hold the binding reference
    inner class SubjectViewHolder(private val binding: ItemPreEnlistedSubjectBinding) :
        RecyclerView.ViewHolder(binding.root) {

        // Bind the subject data to the UI
        fun bind(subject: SubjectsModel) {
            binding.descriptionTextView.text = subject.name
            binding.codeTextView.text = subject.code
            binding.sectionTextView.text = subject.section
            binding.scheduleTextView.text = subject.schedule
            binding.unitTextView.text = subject.units.toString()
        }
    }

    // Create new ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        // Inflate the binding instead of the layout directly
        val binding = ItemPreEnlistedSubjectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
