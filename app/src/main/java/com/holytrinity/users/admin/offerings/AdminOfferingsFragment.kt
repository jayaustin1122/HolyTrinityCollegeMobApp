package com.holytrinity.users.admin.offerings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.holytrinity.R
import com.holytrinity.databinding.FragmentAdminOfferingsBinding

class AdminOfferingsFragment : Fragment() {
    private lateinit var binding: FragmentAdminOfferingsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminOfferingsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbarBackButton.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("selectedFragmentId", null ?: R.id.nav_dashboard_admin)
            }
            findNavController().navigate(R.id.adminDrawerFragment, bundle)
        }
        binding.coursesCard.setOnClickListener {
            findNavController().apply {
                navigate(R.id.adminCoursesFragment)
            }
        }
        binding.curriculumCard.setOnClickListener {
            findNavController().apply {
                navigate(R.id.adminCurriculumsFragment)
            }
        }
        binding.subjectCard.setOnClickListener {
            findNavController().apply {
                navigate(R.id.adminSubjectsFragment)
            }
        }
    }

}