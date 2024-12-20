package com.holytrinity.users.registrar.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.holytrinity.R
import com.holytrinity.api.Subject

class SubjectAdapter(
    private val subjects: MutableList<Subject>,
    private val deleteListener: (Subject) -> Unit,
    private val editListener: (Subject) -> Unit
) : RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder>() {

    inner class SubjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val descriptionTitleTextView: TextView = itemView.findViewById(R.id.descriptionTitleTextView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        val amountTitleTextView: TextView = itemView.findViewById(R.id.amountTitleTextView)
        val amountTextView: TextView = itemView.findViewById(R.id.amountTextView)
        val deleteChip: Chip = itemView.findViewById(R.id.deleteChip)
        val editChip: Chip = itemView.findViewById(R.id.editChip)

        init {
            deleteChip.setOnClickListener {
                val subject = subjects[adapterPosition]
                deleteListener(subject) // Trigger the delete callback
            }
            editChip.setOnClickListener {
                val subject = subjects[adapterPosition]
                Log.d("SubjectAdapter", "Editing subject: $subject")
                editListener(subject) // Trigger the edit callback
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_assessment_fee, parent, false) // Reusing existing layout
        return SubjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        val subject = subjects[position]

        // Set title views programmatically
        holder.titleTextView.text = "Code"
        holder.descriptionTitleTextView.text = "Name"
        holder.amountTitleTextView.text = "Units"

        // Bind subject data to views
        holder.titleTextView.text = subject.code // Display the subject code
        holder.descriptionTextView.text = subject.name
        holder.amountTextView.text = "${subject.units} Units"
    }

    override fun getItemCount(): Int {
        return subjects.size
    }

    fun deleteItem(subject: Subject) {
        val position = subjects.indexOf(subject)
        if (position != -1) {
            subjects.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun updateItems(newSubjects: List<Subject>) {
        subjects.clear()
        subjects.addAll(newSubjects)
        notifyDataSetChanged()
    }
}
