package com.holytrinity.users.registrar.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.holytrinity.R
import com.holytrinity.api.AssessmentFee
import com.holytrinity.api.DiscountFee

class DiscountFeeAdapter(
    private val discountFees: MutableList<DiscountFee>,
    private val deleteListener: (DiscountFee) -> Unit,
    private val editListener: (DiscountFee) -> Unit
) : RecyclerView.Adapter<DiscountFeeAdapter.DiscountFeeViewHolder>() {

    inner class DiscountFeeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val codeTextView: TextView =
            itemView.findViewById(R.id.codeTextView) // New TextView for code
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        val amountTextView: TextView = itemView.findViewById(R.id.amountTextView)
        val deleteChip: Chip = itemView.findViewById(R.id.deleteChip)
        val editChip: Chip = itemView.findViewById(R.id.editChip) // Make sure this is defined in your layout

        init {
            deleteChip.setOnClickListener {
                val fee = discountFees[adapterPosition]
                deleteListener(fee) // Trigger the delete callback
            }
            editChip.setOnClickListener {
                val fee = discountFees[adapterPosition]
                Log.d("fee", "$fee")
                editListener(fee) // Trigger the edit callback
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscountFeeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_discount_fee, parent, false)
        return DiscountFeeViewHolder(view)
    }

    override fun onBindViewHolder(holder: DiscountFeeViewHolder, position: Int) {
        val fee = discountFees[position]
        holder.titleTextView.text = fee.title
        holder.codeTextView.text = fee.code // Set code to the new TextView
        holder.descriptionTextView.text = fee.description
        holder.amountTextView.text = "₱ ${fee.amount}"
    }

    override fun getItemCount(): Int {
        return discountFees.size
    }

    fun deleteItem(fee: DiscountFee) {
        val position = discountFees.indexOf(fee)
        if (position != -1) {
            discountFees.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}
