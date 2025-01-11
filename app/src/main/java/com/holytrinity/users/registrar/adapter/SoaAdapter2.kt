package com.holytrinity.users.registrar.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.holytrinity.databinding.ItemStatementsOfAccountsBinding
import com.holytrinity.databinding.ItemSummaryOfAccountsBinding
import com.holytrinity.model.Soa
import java.text.NumberFormat
import java.util.*
class SoaAdapter2(
    private var soaList: List<Soa>,
    private val studentNames: Map<String, String>,
    private val isRegistrarView: Boolean
) : RecyclerView.Adapter<SoaAdapter2.SoaViewHolder>() {

    private var filteredList: List<Soa> = soaList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoaViewHolder {
        val binding = ItemStatementsOfAccountsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
        holder.bind(soa, studentNames[soa.student_id] ?: "Unknown", position, isRegistrarView)
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    class SoaViewHolder(private val binding: ItemStatementsOfAccountsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(soa: Soa, studentName: String, position: Int, isRegistrarView: Boolean) {
            binding.tvNo.text = (position + 1).toString()

            if (isRegistrarView) {
                binding.tvAmount.visibility = View.VISIBLE
                binding.tvPaid.visibility = View.GONE
                binding.tvNo.visibility = View.GONE
                binding.tvAmount.text = formatCurrency(soa.balance)
                binding.tvIdNo.text = "Balance"
                updateMargin(binding.tvIdNo, marginStartDp = 30, marginEndDp = 100)
            } else {
                binding.tvIdNo.text = soa.student_id
                binding.tvAmount.visibility = View.VISIBLE
                binding.tvPaid.visibility = View.VISIBLE
                binding.tvNo.visibility = View.VISIBLE
                binding.tvAmount.text = formatCurrency(soa.balance)
                binding.tvPaid.text = formatCurrency(soa.amount)
                updateMargin(binding.tvIdNo, marginStartDp = 0, marginEndDp = 0)
            }
        }

        private fun updateMargin(view: View, marginStartDp: Int, marginEndDp: Int) {
            val params = view.layoutParams as ViewGroup.MarginLayoutParams
            val density = view.context.resources.displayMetrics.density
            params.marginStart = (marginStartDp * density).toInt()
            params.marginEnd = (marginEndDp * density).toInt()
            view.layoutParams = params
        }

        private fun formatCurrency(amount: Double): String {
            val format = NumberFormat.getCurrencyInstance(Locale("en", "PH"))
            return format.format(amount)
        }
    }

}


