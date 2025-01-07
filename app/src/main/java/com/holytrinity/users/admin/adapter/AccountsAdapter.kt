package com.holytrinity.users.admin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.holytrinity.databinding.ItemSummaryOfAccountsBinding
import com.holytrinity.model.Account

class AccountsAdapter(private var accounts: List<Account> , private val onAccountClick: (Int) -> Unit) : RecyclerView.Adapter<AccountsAdapter.AccountViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        val binding = ItemSummaryOfAccountsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AccountViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        val account = accounts[position]
        holder.bind(account)
    }

    override fun getItemCount(): Int = accounts.size


    fun updateData(newAccounts: List<Account>) {
        accounts = newAccounts
        notifyDataSetChanged()
    }

    inner class AccountViewHolder(private val binding: ItemSummaryOfAccountsBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(account: Account) {
            binding.tvNo.text = (adapterPosition + 1).toString()
            binding.tvIdNo.text = getRoleName(account.role_id)
            binding.tvName.text = account.name
            binding.tvAmount.visibility = View.GONE
            if (account.role_id == 10 || account.role_id == 6) {
                binding.root.setOnClickListener {
                    onAccountClick(account.user_id)
                }
            } else {
                binding.root.setOnClickListener(null)
            }
        }

        private fun getRoleName(roleId: Int): String {
            return when (roleId) {
                1 -> "Administrator"
                3 -> "Accounting"
                8 -> "Assistant"
                10 -> "Benefactor"
                4 -> "Cashiering"
                9 -> "Counselor"
                5 -> "Instructor"
                6 -> "Parent"
                2 -> "Registrar"
                7 -> "Student"
                else -> "Unknown Role"
            }
        }
    }
}
