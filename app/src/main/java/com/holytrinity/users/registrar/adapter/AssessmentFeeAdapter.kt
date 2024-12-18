package com.holytrinity.users.registrar.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.holytrinity.R
import com.holytrinity.api.AssessmentFee

class AssessmentFeeAdapter(
    private val assessmentFees: MutableList<AssessmentFee>,
    private val deleteListener: (AssessmentFee) -> Unit,
    private val editListener: (AssessmentFee) -> Unit
) : RecyclerView.Adapter<AssessmentFeeAdapter.AssessmentFeeViewHolder>() {

    inner class AssessmentFeeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        val amountTextView: TextView = itemView.findViewById(R.id.amountTextView)
        val deleteChip: Chip = itemView.findViewById(R.id.deleteChip)
        val editChip: Chip = itemView.findViewById(R.id.editChip) // Make sure this is defined in your layout

        init {
            deleteChip.setOnClickListener {
                val fee = assessmentFees[adapterPosition]
                deleteListener(fee)  // Trigger the delete callback
            }
            editChip.setOnClickListener {
                val fee = assessmentFees[adapterPosition]
                editListener(fee) // Trigger the edit callback
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssessmentFeeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_assessment_fee, parent, false)
        return AssessmentFeeViewHolder(view)
    }

    override fun onBindViewHolder(holder: AssessmentFeeViewHolder, position: Int) {
        val fee = assessmentFees[position]
        holder.titleTextView.text = fee.title
        holder.descriptionTextView.text = fee.description
        holder.amountTextView.text = "₱ ${fee.amount}"
    }

    override fun getItemCount(): Int {
        return assessmentFees.size
    }

    fun deleteItem(fee: AssessmentFee) {
        val position = assessmentFees.indexOf(fee)
        if (position != -1) {
            assessmentFees.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}
