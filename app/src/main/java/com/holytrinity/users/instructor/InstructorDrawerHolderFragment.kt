package com.holytrinity.users.instructor

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.holytrinity.R
import com.holytrinity.databinding.FragmentInstructorDrawerHolderBinding

class InstructorDrawerHolderFragment : Fragment() {

    private lateinit var binding: FragmentInstructorDrawerHolderBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentInstructorDrawerHolderBinding.inflate(layoutInflater)
        return binding.root
    }
}