package com.holytrinity.users.student

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
import com.holytrinity.databinding.FragmentStudentDrawerHolderBinding
import com.holytrinity.users.registrar.dashboard.RegistrarDashBoardFragment
import com.holytrinity.users.student.calendar.StudentCalendarFragment
import com.holytrinity.users.student.changepass.StudentChangePassFragment
import com.holytrinity.users.student.curr_eval.StudentCurrEvalFragment
import com.holytrinity.users.student.dashboard.StudentDashboardFragment
import com.holytrinity.users.student.due_amount.StudentDueAmountFragment
import com.holytrinity.users.student.enrolled_subs.StudentEnrolledSubsFragment
import com.holytrinity.users.student.profile.StudentProfileFragment
import com.holytrinity.users.student.soa.StudentSoaFragment
import com.holytrinity.util.LogoutHelper


class StudentDrawerHolderFragment : Fragment() {
    private lateinit var binding : FragmentStudentDrawerHolderBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: Toolbar
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStudentDrawerHolderBinding.inflate(inflater, container, false)
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
            openFragment(StudentDashboardFragment())
        }

        // Set Navigation Item click listener
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_dashboard_student -> openFragment(StudentDashboardFragment())
                R.id.nav_student_profile_student -> openFragment(StudentProfileFragment())
                R.id.nav_due_amount_student -> openFragment(StudentDueAmountFragment())
                R.id.nav_enrolled_subjects_student -> openFragment(StudentEnrolledSubsFragment())
                R.id.nav_curriculum_evaluation_student -> openFragment(StudentCurrEvalFragment())
                R.id.nav_statement_of_account_student -> openFragment(StudentSoaFragment())
                R.id.nav_school_calendar_student -> openFragment(StudentCalendarFragment())
                R.id.nav_change_password_student -> openFragment(StudentChangePassFragment())
           //     R.id.nav_student_help -> openFragment()
                R.id.nav_logout_student -> {
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

        if (fragment is StudentDashboardFragment) {
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
