package com.holytrinity.users.registrar.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.holytrinity.databinding.ItemPreEnlistedSubjectBinding
import com.holytrinity.model.Subjects

class SubjectEnrollmentAdapter(private val subjects: List<Subjects>) : RecyclerView.Adapter<SubjectEnrollmentAdapter.SubjectViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        val binding = ItemPreEnlistedSubjectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SubjectViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        val subject = subjects[position] // Get the current subject

        // Check if the subject has any classes. We'll safely handle an empty class list
        val classInfo = subject.classes.firstOrNull() // Get the first class, or null if no classes exist

        // Binding data to the view holder
        holder.binding.apply {
            // Set subject details
            codeTextView.text = subject.code
            descriptionTextView.text = subject.name
            unitTextView.text = subject.units.toString()

            // If class info exists, set it. If not, set a default text or hide the section views.
            classInfo?.let {
                sectionTextView.text = it.section
                scheduleTextView.text = it.schedule
            } ?: run {
                sectionTextView.text = "No section available"
                scheduleTextView.text = "No schedule available"
            }
        }
    }

    override fun getItemCount(): Int {
        return subjects.size // Return the size of the subjects list
    }

    inner class SubjectViewHolder(val binding: ItemPreEnlistedSubjectBinding) : RecyclerView.ViewHolder(binding.root)
}
