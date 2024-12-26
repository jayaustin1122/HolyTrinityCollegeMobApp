package com.holytrinity.users.registrar.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.holytrinity.R
import com.holytrinity.api.Curriculum

class CurriculumAdapter(
    private val curriculums: MutableList<Curriculum>,
    private val deleteListener: (Curriculum) -> Unit,
    private val editListener: (Curriculum) -> Unit,
    private val onItemClick: (Curriculum) -> Unit
) : RecyclerView.Adapter<CurriculumAdapter.CurriculumViewHolder>() {

    inner class CurriculumViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val codeTextView: TextView = itemView.findViewById(R.id.codeTextView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        val amountTextView: TextView = itemView.findViewById(R.id.amountTextView) // This will be hidden
        val descriptionTitleTextView: TextView = itemView.findViewById(R.id.descriptionTitleTextView)
        val amountTitleTextView: TextView = itemView.findViewById(R.id.amountTitleTextView) // This will be hidden
        val deleteChip: Chip = itemView.findViewById(R.id.deleteChip)
        val editChip: Chip = itemView.findViewById(R.id.editChip)
        val itemLayout: View = itemView.findViewById(R.id.itemLayout)

        init {
            deleteChip.setOnClickListener {
                val curriculum = curriculums[adapterPosition]
                deleteListener(curriculum)
            }
            editChip.setOnClickListener {
                val curriculum = curriculums[adapterPosition]
                Log.d("CurriculumAdapter", "Editing: $curriculum")
                editListener(curriculum)
            }
            itemLayout.setOnClickListener {
                val curriculum = curriculums[adapterPosition]
                onItemClick(curriculum)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurriculumViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_discount_fee, parent, false) // Reusing existing layout
        return CurriculumViewHolder(view)
    }

    override fun onBindViewHolder(holder: CurriculumViewHolder, position: Int) {
        val curriculum = curriculums[position]

        // Set the main title to Curriculum Name
        holder.titleTextView.text = curriculum.name
        // Set the code
        holder.codeTextView.text = curriculum.code
        // Set the description
        holder.descriptionTextView.text = curriculum.description

        // Hide the amount fields as they are not needed for Curriculum
        holder.amountTextView.visibility = View.GONE
        holder.amountTitleTextView.visibility = View.GONE

        // Optionally, you can change the description title if needed
        holder.descriptionTitleTextView.text = "Description"
    }

    override fun getItemCount(): Int {
        return curriculums.size
    }

    fun deleteItem(curriculum: Curriculum) {
        val position = curriculums.indexOf(curriculum)
        if (position != -1) {
            curriculums.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    // Optional: Method to update the list
    fun updateCurriculums(newCurriculums: List<Curriculum>) {
        curriculums.clear()
        curriculums.addAll(newCurriculums)
        notifyDataSetChanged()
    }
}
