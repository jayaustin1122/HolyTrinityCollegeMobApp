package com.holytrinity.users.registrar.fee_management

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.holytrinity.R
import com.holytrinity.databinding.FragmentCashierFeeManagementHolderBinding
import com.holytrinity.users.registrar.adapter.MyFragmentStateAdapter
import com.holytrinity.users.registrar.registrar.RegistarEnrollmentSubjectFragment
import com.holytrinity.users.registrar.registrar.RegistrarEnrollmentListFragment


class CashierFeeManagementHolderFragment : Fragment() {
    private lateinit var binding : FragmentCashierFeeManagementHolderBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCashierFeeManagementHolderBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabLayout = binding.tabLayout
        val viewPager2 = binding.pager

        // Pass the fragments dynamically
        val myAdapter = MyFragmentStateAdapter(
            this@CashierFeeManagementHolderFragment.requireActivity(),
            listOf(
                RegistrarFeeTab1Fragment(), // First fragment
                RegistrarFeeTab2Fragment() // Second fragment
            )
        )
        viewPager2.adapter = myAdapter

        TabLayoutMediator(tabLayout, viewPager2) { tab: TabLayout.Tab, position: Int ->
            when (position) {
                0 -> tab.text = "Assessment Management"
                1 -> tab.text = "Discount Management"
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