package com.holytrinity.users.admin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.holytrinity.databinding.ItemSummaryOfAccountsBinding
import com.holytrinity.model.Account

class AccountsAdapter(private var accounts: List<Account>) : RecyclerView.Adapter<AccountsAdapter.AccountViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        val binding = ItemSummaryOfAccountsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AccountViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        val account = accounts[position]
        holder.bind(account)
    }

    override fun getItemCount(): Int = accounts.size

    // Method to update the data in the adapter
    fun updateData(newAccounts: List<Account>) {
        accounts = newAccounts
        notifyDataSetChanged() // Notifies the adapter that the data set has changed
    }

    inner class AccountViewHolder(private val binding: ItemSummaryOfAccountsBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(account: Account) {
            // Set the row number
            binding.tvNo.text = (adapterPosition + 1).toString()

            // Set the role name based on role_id
            binding.tvIdNo.text = getRoleName(account.role_id)

            // Set the user's name
            binding.tvName.text = account.name

            // Hide the 'Amount' TextView (as per your layout example)
            binding.tvAmount.visibility = View.GONE
        }

        // Function to map role_id to role name
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
                else -> "Unknown Role" // Default in case of an unrecognized role_id
            }
        }
    }
}
