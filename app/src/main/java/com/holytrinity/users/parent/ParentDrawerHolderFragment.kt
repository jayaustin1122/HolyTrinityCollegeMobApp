package com.holytrinity.users.parent

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
import com.holytrinity.databinding.FragmentParentDrawerHolderBinding
import com.holytrinity.users.benefactor.beneficiaries.BenefactorBeneficiariesFragment
import com.holytrinity.users.benefactor.schoolcalendar.BenefactorSchoolCalendarFragment
import com.holytrinity.users.parent.child.ParentChildFragment
import com.holytrinity.users.parent.schoolcalendar.ParentSchoolCalendarFragment
import com.holytrinity.util.LogoutHelper


class ParentDrawerHolderFragment : Fragment() {

    private lateinit var binding : FragmentParentDrawerHolderBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentParentDrawerHolderBinding.inflate(layoutInflater)
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
            openFragment(ParentChildFragment())
        }

        // Set Navigation Item click listener
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_child_parent -> openFragment(ParentChildFragment())
                R.id.nav_calendar_parent -> openFragment(ParentSchoolCalendarFragment())
                R.id.nav_logout_parent -> {
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
            .replace(R.id.fragment_containerss, fragment)
            .commit()

        val constraintLayout = binding.constraint
        val constraintSet = ConstraintSet()

        constraintSet.clone(constraintLayout)

        if (fragment is ParentChildFragment) {
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
                R.id.fragment_containerss,
                ConstraintSet.TOP,
                ConstraintSet.PARENT_ID,
                ConstraintSet.TOP
            )
            constraintSet.connect(
                R.id.fragment_containerss,
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