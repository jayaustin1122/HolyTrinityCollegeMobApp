package com.holytrinity.users.admin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.holytrinity.R
import com.holytrinity.api.Subject

class CurriculumSubjectAdapter(
    private var subjects: List<Subject>,
    private val onSubjectClick: (Subject) -> Unit
) : RecyclerView.Adapter<CurriculumSubjectAdapter.SubjectViewHolder>() {

    inner class SubjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val codeTextView: TextView = itemView.findViewById(R.id.subjectCodeTextView)
        val nameTextView: TextView = itemView.findViewById(R.id.subjectNameTextView)
        val unitsTextView: TextView = itemView.findViewById(R.id.subjectUnitsTextView)

        init {
            itemView.setOnClickListener {
                val subject = subjects[adapterPosition]
                onSubjectClick(subject)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_subject_card, parent, false)
        return SubjectViewHolder(view)
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        val subject = subjects[position]
        holder.codeTextView.text = subject.code
        holder.nameTextView.text = subject.name
        holder.unitsTextView.text = subject.units.toString() // Kung Int
    }

    override fun getItemCount(): Int = subjects.size

    fun updateList(newSubjects: List<Subject>) {
        subjects = newSubjects
        notifyDataSetChanged()
    }

    fun removeItem(subject: Subject) {
        val mutableList = subjects.toMutableList()
        val index = mutableList.indexOfFirst { it.subject_id == subject.subject_id }
        if (index != -1) {
            mutableList.removeAt(index)
            subjects = mutableList
            notifyItemRemoved(index)
        }
    }
}
