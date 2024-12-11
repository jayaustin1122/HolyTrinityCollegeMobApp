package com.holytrinity.users.cashier

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.findNavController
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.canorecoapp.utils.DialogUtils
import com.holytrinity.R
import com.holytrinity.databinding.FragmentCashierDrawerBinding
import com.holytrinity.users.cashier.dashboard.CashierDashboardFragment
import com.holytrinity.users.cashier.payment_management.CashierPaymentManagementFragment
import com.holytrinity.users.registrar.fee_management.CashierFeeManagementHolderFragment
import com.holytrinity.users.setup.SetUpFragment
import com.holytrinity.util.LogoutHelper


class CashierDrawerFragment : Fragment() {
    private lateinit var binding: FragmentCashierDrawerBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var loadingDialog: SweetAlertDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCashierDrawerBinding.inflate(layoutInflater)
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
            openFragment(CashierDashboardFragment())
        }

        // Set Navigation Item click listener
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_cashier_dashboard -> openFragment(CashierDashboardFragment())
                R.id.nav_payment_management -> openFragment(CashierPaymentManagementFragment())
                R.id.nav_payment_transaction -> openFragment(CashierFeeManagementHolderFragment())
                R.id.nav_cashier_setup -> openFragment(SetUpFragment())
                R.id.nav_cashier_logout -> {
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
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}