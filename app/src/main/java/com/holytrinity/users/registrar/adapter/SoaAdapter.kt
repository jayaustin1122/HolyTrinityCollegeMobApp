package com.holytrinity.users.registrar.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.holytrinity.databinding.ItemSummaryOfAccountsBinding
import com.holytrinity.model.Soa

class SoaAdapter(private val soaList: List<Soa>) : RecyclerView.Adapter<SoaAdapter.SoaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoaViewHolder {
        // Inflate the layout using ViewBinding
        val binding = ItemSummaryOfAccountsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SoaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SoaViewHolder, position: Int) {
        val soa = soaList[position]
        holder.bind(soa, position)
    }

    override fun getItemCount(): Int {
        return soaList.size
    }

    // Update ViewHolder to use ViewBinding
    class SoaViewHolder(private val binding: ItemSummaryOfAccountsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(soa: Soa, position: Int) {
            // Bind data to the views
            binding.tvNo.text = (position + 1).toString() // Display position as No
            binding.tvIdNo.text = soa.studentID
            binding.tvName.text = soa.transaction
            binding.tvAmount.text = soa.due_amount
        }
    }
}
