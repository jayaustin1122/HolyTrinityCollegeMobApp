package com.holytrinity.users.registrar.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.holytrinity.databinding.ItemSummaryOfAccountsBinding
import com.holytrinity.model.Soa
import java.text.NumberFormat
import java.util.*

class SoaAdapter(
    private var soaList: List<Soa>,
    private val studentNames: Map<String, String>
) : RecyclerView.Adapter<SoaAdapter.SoaViewHolder>() {

    private var filteredList: List<Soa> = soaList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoaViewHolder {
        val binding = ItemSummaryOfAccountsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SoaViewHolder(binding)
    }

    fun updateSoaList(newSoaList: List<Soa>) {
        this.soaList = newSoaList
        this.filteredList = newSoaList
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        filteredList = if (query.isEmpty()) {
            soaList
        } else {
            val lowerCaseQuery = query.lowercase()
            soaList.filter {

                studentNames[it.student_id]?.lowercase()?.contains(lowerCaseQuery) == true ||
                        it.student_id.lowercase().contains(lowerCaseQuery)
            }
        }
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: SoaViewHolder, position: Int) {
        val soa = filteredList[position]
        holder.bind(soa, studentNames[soa.student_id] ?: "Unknown", position)
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    class SoaViewHolder(private val binding: ItemSummaryOfAccountsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(soa: Soa, studentName: String, position: Int) {
            binding.tvNo.text = (position + 1).toString()
            binding.tvIdNo.text = soa.student_id
            binding.tvName.text = soa.student_name

            // Format the balance to Peso format
            val formattedBalance = formatCurrency(soa.balance)
            binding.tvAmount.text = formattedBalance
        }

        private fun formatCurrency(amount: Double): String {
            val format = NumberFormat.getCurrencyInstance(Locale("en", "PH"))
            return format.format(amount)
        }
    }
}
