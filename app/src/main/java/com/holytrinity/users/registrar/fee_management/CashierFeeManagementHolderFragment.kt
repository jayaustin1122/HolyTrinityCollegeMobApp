package com.holytrinity.users.registrar.fee_management

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.holytrinity.R
import com.holytrinity.databinding.FragmentCashierFeeManagementHolderBinding
import com.holytrinity.users.registrar.adapter.MyFragmentStateAdapter
import com.holytrinity.users.registrar.registrar.RegistarEnrollmentSubjectFragment
import com.holytrinity.users.registrar.registrar.RegistrarEnrollmentListFragment
import com.holytrinity.util.UserPreferences


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

        val roleId = UserPreferences.getRoleId(requireContext())
        binding.toolbarBackButton.setOnClickListener {
            navigateBasedOnRole(roleId)
        }
    }

    private fun navigateBasedOnRole(roleId: Int) {
        when (roleId) {
            1 -> findNavController().navigate(R.id.adminDrawerFragment)
            2 -> findNavController().navigate(R.id.registrarDrawerHolderFragment)
            4 -> findNavController().navigate(R.id.cashierDrawerFragment)
            5 -> findNavController().navigate(R.id.instructorDrawerHolderFragment)
            6 -> findNavController().navigate(R.id.parentDrawerHolderFragment)
            7 -> findNavController().navigate(R.id.studentDrawerHolderFragment)
            10 -> findNavController().navigate(R.id.benefactorDrawerHolderFragment)
            else -> {
                // Default navigation if roleId doesn't match any of the above
                Toast.makeText(
                    requireContext(),
                    "Invalid role, navigating back to dashboard",
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().navigate(R.id.nav_dashboard) // You can replace this with a default fragment
            }
        }
    }
}