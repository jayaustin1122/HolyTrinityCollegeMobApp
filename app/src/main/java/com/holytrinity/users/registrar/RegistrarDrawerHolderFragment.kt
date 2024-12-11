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
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.holytrinity.R
import com.holytrinity.databinding.FragmentRegistrarDrawerHolderBinding
import com.holytrinity.users.registrar.dashboard.RegistrarDashBoardFragment
import com.holytrinity.users.registrar.enrollment.enroll.RegistrarEnrollmentFragment
import com.holytrinity.users.registrar.studentledger.RegistrarStudentLedgerFragment

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
                R.id.nav_general -> openFragment(RegistrarDashBoardFragment())
                R.id.nav_enrollment -> openFragment(RegistrarEnrollmentFragment())
                R.id.nav_ledger -> openFragment(RegistrarStudentLedgerFragment())
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
