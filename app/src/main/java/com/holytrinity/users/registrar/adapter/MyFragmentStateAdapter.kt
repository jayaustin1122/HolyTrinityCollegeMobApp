package com.holytrinity.users.registrar.adapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.holytrinity.users.registrar.studentledger.StatementsOfAccountsLedgerFragment
import com.holytrinity.users.registrar.studentledger.SummaryOfAccountsLedgerFragment

class MyFragmentStateAdapter(fragmentActivity: FragmentActivity): FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> SummaryOfAccountsLedgerFragment()
            1 -> StatementsOfAccountsLedgerFragment()

            else ->  SummaryOfAccountsLedgerFragment()
        }
    }
}