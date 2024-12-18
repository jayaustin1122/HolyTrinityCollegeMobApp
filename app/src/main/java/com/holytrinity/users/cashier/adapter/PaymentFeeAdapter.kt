package com.holytrinity.users.cashier.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.holytrinity.R
import com.holytrinity.api.PaymentFee

class PaymentFeeAdapter(
    private val payments: MutableList<PaymentFee>,
    private val deleteListener: (PaymentFee) -> Unit
) : RecyclerView.Adapter<PaymentFeeAdapter.PaymentFeeViewHolder>() {

    inner class PaymentFeeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        val amountTextView: TextView = itemView.findViewById(R.id.amountTextView)
        val deleteChip: Chip = itemView.findViewById(R.id.deleteChip)

        init {
            deleteChip.setOnClickListener {
                val payment = payments[adapterPosition]
                deleteListener(payment)  // Trigger the delete callback
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentFeeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_payment_fee, parent, false)
        return PaymentFeeViewHolder(view)
    }

    override fun onBindViewHolder(holder: PaymentFeeViewHolder, position: Int) {
        val payment = payments[position]
        holder.titleTextView.text = payment.title
        holder.descriptionTextView.text = payment.description
        holder.amountTextView.text = "₱ ${payment.amount}"
    }

    override fun getItemCount(): Int {
        return payments.size
    }

    fun deleteItem(payment: PaymentFee) {
        val position = payments.indexOf(payment)
        if (position != -1) {
            payments.removeAt(position)
            notifyItemRemoved(position)
        }
    }
}
