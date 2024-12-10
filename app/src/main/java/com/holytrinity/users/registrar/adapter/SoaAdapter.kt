package com.holytrinity.users.registrar.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.holytrinity.databinding.ItemSummaryOfAccountsBinding
import com.holytrinity.model.Soa

class SoaAdapter(
    private val soaList: List<Soa>,
    private val studentNames: Map<String, String> // Add a map for studentId to studentName
) : RecyclerView.Adapter<SoaAdapter.SoaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoaViewHolder {
        val binding = ItemSummaryOfAccountsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SoaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SoaViewHolder, position: Int) {
        val soa = soaList[position]
        holder.bind(soa, studentNames[soa.studentID] ?: "Unknown", position)
    }

    override fun getItemCount(): Int {
        return soaList.size
    }

    class SoaViewHolder(private val binding: ItemSummaryOfAccountsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(soa: Soa, studentName: String, position: Int) {
            binding.tvNo.text = (position + 1).toString()
            binding.tvIdNo.text = soa.studentID
            binding.tvName.text = studentName
            binding.tvAmount.text = soa.due_amount
        }
    }
}
