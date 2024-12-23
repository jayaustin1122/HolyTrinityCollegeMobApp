package com.holytrinity.users.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.findNavController
import com.holytrinity.R
import com.holytrinity.databinding.FragmentAdminDrawerHolderBinding
import com.holytrinity.users.admin.dashboard.AdminDashboardFragment
import com.holytrinity.users.admin.manage_account.AdminManageAccountFragment
import com.holytrinity.users.admin.offerings.AdminOfferingsFragment
import com.holytrinity.users.cashier.payment_management.CashierPaymentManagementFragment
import com.holytrinity.users.cashier.payment_transaction.CashierPaymentHolderFragment
import com.holytrinity.users.registrar.admisson.RegistrarAdmissionFragment
import com.holytrinity.users.registrar.assestment.AssesmentFragment
import com.holytrinity.users.registrar.dashboard.RegistrarDashBoardFragment
import com.holytrinity.users.registrar.enrollment.enroll.RegistrarEnrollmentFragment
import com.holytrinity.users.registrar.fee_management.CashierFeeManagementHolderFragment
import com.holytrinity.users.registrar.registrar.RegistrarHolderFragment
import com.holytrinity.users.registrar.studentledger.RegistrarStudentLedgerFragment
import com.holytrinity.users.setup.SetUpFragment
import com.holytrinity.util.LogoutHelper


class AdminDrawerHolderFragment : Fragment() {

    private lateinit var binding : FragmentAdminDrawerHolderBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminDrawerHolderBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        drawerLayout = binding.myDrawerLayout

        toolbar = binding.toolbar
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)

        actionBarDrawerToggle = ActionBarDrawerToggle(
            requireActivity(),
            drawerLayout,
            toolbar,
            R.string.nav_open,
            R.string.nav_close
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        actionBarDrawerToggle.drawerArrowDrawable.color = resources.getColor(R.color.white);
        setHasOptionsMenu(true)

        // Open Dashboard fragment on first load
        if (savedInstanceState == null) {
            openFragment(AdminDashboardFragment())
        }

        // Set Navigation Item click listener
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_dashboard_admin -> openFragment(AdminDashboardFragment())
                R.id.nav_manage_account_admin -> openFragment(AdminManageAccountFragment())
                R.id.nav_offerings_admin -> openFragment(AdminOfferingsFragment())
                R.id.nav_logout_admin -> {
                    LogoutHelper.logout(requireContext()) {
                        findNavController().navigate(R.id.signInFragment)
                    }
                }
                else -> false
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun openFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()

        val constraintLayout = binding.constraint
        val constraintSet = ConstraintSet()

        constraintSet.clone(constraintLayout)

        if (fragment is AdminDashboardFragment) {
            binding.toolbar.visibility = View.VISIBLE
            constraintSet.connect(
                R.id.fragment_container,
                ConstraintSet.BOTTOM,
                R.id.toolbar,
                ConstraintSet.TOP
            )
        } else {
            binding.toolbar.visibility = View.GONE
            constraintSet.connect(
                R.id.fragment_container,
                ConstraintSet.TOP,
                ConstraintSet.PARENT_ID,
                ConstraintSet.TOP
            )
            constraintSet.connect(
                R.id.fragment_container,
                ConstraintSet.BOTTOM,
                ConstraintSet.PARENT_ID,
                ConstraintSet.BOTTOM
            )
        }
        constraintSet.applyTo(constraintLayout)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}