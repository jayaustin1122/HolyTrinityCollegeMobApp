package com.holytrinity.users.registrar.enrollment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import com.holytrinity.R
import com.holytrinity.databinding.FragmentRegistrarEnrollmentBinding
import com.holytrinity.users.registrar.adapter.EnrollmentAdapter


class RegistrarEnrollmentFragment : Fragment() {
    private lateinit var binding : FragmentRegistrarEnrollmentBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegistrarEnrollmentBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentList = arrayListOf<Fragment>(
            StepOneEnrollmentFragment(),
            StepTwoEnrollmentFragment(),
            StepThreeEnrollmentFragment()
        )

        val adapter = EnrollmentAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        binding.viewPager.adapter = adapter

        // Link TabLayout with ViewPager2 for dots indicator
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { _, _ -> }.attach()

        binding.backButton.setOnClickListener {
            val currentItem = binding.viewPager.currentItem
            if (currentItem < fragmentList.size - 1) {
                binding.viewPager.currentItem = currentItem - 1
            } else {
                // If on the last page, you can navigate to another screen
                findNavController().navigate(R.id.registrarDrawerHolderFragment)
            }
        }

        // Handle Next button click
        binding.nextButton.setOnClickListener {
            val currentItem = binding.viewPager.currentItem
            if (currentItem < fragmentList.size - 1) {
                binding.viewPager.currentItem = currentItem + 1
            } else {
                // If on the last page, you can navigate to another screen
                findNavController().navigate(R.id.registrarDrawerHolderFragment)
            }
        }
    }

}