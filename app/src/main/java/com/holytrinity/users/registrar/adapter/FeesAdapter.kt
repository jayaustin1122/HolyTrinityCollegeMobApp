package com.holytrinity.users.registrar.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.holytrinity.databinding.ItemFeeBinding
import com.holytrinity.model.Fees
import java.text.NumberFormat
import java.util.*

class FeesAdapter : ListAdapter<Fees, FeesAdapter.FeeViewHolder>(FeeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeeViewHolder {
        val binding = ItemFeeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FeeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FeeViewHolder, position: Int) {
        val fee = getItem(position)
        holder.bind(fee)
    }

    class FeeViewHolder(private val binding: ItemFeeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(fee: Fees) {

            val formattedAmount = NumberFormat.getCurrencyInstance(Locale("en", "PH")).format(fee.amount)

            binding.titleTextView.text = fee.title
            binding.amountTextView.text = formattedAmount
            binding.descriptionTextView.text = fee.description ?: "No description available"
        }
    }

    class FeeDiffCallback : DiffUtil.ItemCallback<Fees>() {
        override fun areItemsTheSame(oldItem: Fees, newItem: Fees): Boolean {
            return oldItem.fee_id == newItem.fee_id
        }

        override fun areContentsTheSame(oldItem: Fees, newItem: Fees): Boolean {
            return oldItem == newItem
        }
    }
}
