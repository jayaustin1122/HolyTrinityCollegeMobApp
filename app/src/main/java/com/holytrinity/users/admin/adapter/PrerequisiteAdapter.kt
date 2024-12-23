// PrerequisiteAdapter.kt
package com.holytrinity.users.admin.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.holytrinity.databinding.ItemPreRequisitesBinding
import com.holytrinity.model.PrerequisiteWithDetails

class PrerequisiteAdapter(
    private var prerequisites: List<PrerequisiteWithDetails>,
    private val onEditClick: (PrerequisiteWithDetails) -> Unit,
    private val onDeleteClick: (PrerequisiteWithDetails) -> Unit
) : RecyclerView.Adapter<PrerequisiteAdapter.PrerequisiteViewHolder>() {

    inner class PrerequisiteViewHolder(private val binding: ItemPreRequisitesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(prerequisite: PrerequisiteWithDetails) {
            binding.subjectNameTextView.text = prerequisite.subjectName
            binding.prerequisiteTextView.text = prerequisite.prerequisiteSubjectName
            binding.curriculumTextView.text = prerequisite.curriculum?.let {
                "Year ${it.recommended_year_level}, Sem ${it.recommended_sem}"
            } ?: "N/A"

            binding.editChip.setOnClickListener {
                onEditClick(prerequisite)
            }

            binding.deleteChip.setOnClickListener {
                onDeleteClick(prerequisite)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrerequisiteViewHolder {
        val binding = ItemPreRequisitesBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PrerequisiteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PrerequisiteViewHolder, position: Int) {
        holder.bind(prerequisites[position])
    }

    override fun getItemCount(): Int = prerequisites.size

    fun updateData(newPrerequisites: List<PrerequisiteWithDetails>) {
        prerequisites = newPrerequisites
        notifyDataSetChanged()
    }
}
