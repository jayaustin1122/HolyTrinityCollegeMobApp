package com.holytrinity.users.instructor.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.holytrinity.R
import com.holytrinity.databinding.FragmentInstructorDashboardBinding
import com.holytrinity.util.UserPreferences

class InstructorDashboardFragment : Fragment() {

    private lateinit var binding: FragmentInstructorDashboardBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =  FragmentInstructorDashboardBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.textViewUser.text = UserPreferences.getName(requireContext())
    }
}