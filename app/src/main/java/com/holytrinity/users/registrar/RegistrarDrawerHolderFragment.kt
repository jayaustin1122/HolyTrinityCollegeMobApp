package com.holytrinity.users.registrar

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
import com.holytrinity.databinding.FragmentRegistrarDrawerHolderBinding
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

class RegistrarDrawerHolderFragment : Fragment() {
    private lateinit var binding: FragmentRegistrarDrawerHolderBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegistrarDrawerHolderBinding.inflate(inflater, container, false)
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
            openFragment(RegistrarDashBoardFragment())
        }

        // Set Navigation Item click listener
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_dashboard -> openFragment(RegistrarDashBoardFragment())
                R.id.nav_admission -> openFragment(RegistrarAdmissionFragment())
                R.id.nav_enrollment -> openFragment(RegistrarEnrollmentFragment())
                R.id.nav_ledger -> openFragment(RegistrarStudentLedgerFragment())
                R.id.nav_payment_management -> openFragment(CashierPaymentManagementFragment())
                R.id.nav_payment -> openFragment(CashierPaymentHolderFragment())
                R.id.nav_fee_management -> openFragment(CashierFeeManagementHolderFragment())
                R.id.nav_registrar -> openFragment(RegistrarHolderFragment())
                R.id.nav_assessment -> openFragment(AssesmentFragment())
                R.id.nav_setup -> openFragment(SetUpFragment())
                R.id.nav_logout -> {
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

        if (fragment is RegistrarDashBoardFragment) {
            binding.toolbar.visibility = View.VISIBLE
            constraintSet.connect(
                R.id.fragment_container,
                ConstraintSet.TOP,
                R.id.toolbar,
                ConstraintSet.BOTTOM
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
