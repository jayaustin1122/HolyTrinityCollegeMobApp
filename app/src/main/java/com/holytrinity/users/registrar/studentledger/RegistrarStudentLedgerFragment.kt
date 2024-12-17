package com.holytrinity.users.registrar.studentledger

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.holytrinity.R
import com.holytrinity.databinding.FragmentRegistrarStudentLedgerBinding
import com.holytrinity.users.registrar.adapter.MyFragmentStateAdapter

class RegistrarStudentLedgerFragment : Fragment() {
    private lateinit var binding: FragmentRegistrarStudentLedgerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrarStudentLedgerBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabLayout = binding.tabLayout
        val viewPager2 = binding.pager

        // Pass the fragments dynamically
        val myAdapter = MyFragmentStateAdapter(
            this@RegistrarStudentLedgerFragment.requireActivity(),
            listOf(
                SummaryOfAccountsLedgerFragment(), // First fragment
                StatementsOfAccountsLedgerFragment() // Second fragment
            )
        )
        viewPager2.adapter = myAdapter

        TabLayoutMediator(tabLayout, viewPager2) { tab: TabLayout.Tab, position: Int ->
            when (position) {
                0 -> tab.text = "Summary of Accounts"
                1 -> tab.text = "Statement of Accounts"
            }
        }.attach()

        binding.toolbarBackButton.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("selectedFragmentId", null ?: R.id.nav_dashboard)
            }
            findNavController().navigate(R.id.registrarDrawerHolderFragment, bundle)
        }
    }
}
