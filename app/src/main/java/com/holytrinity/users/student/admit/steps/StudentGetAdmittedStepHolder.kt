package com.holytrinity.users.student.admit.steps

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.holytrinity.databinding.FragmentStudentGetAdmittedStepHolderBinding


class StudentGetAdmittedStepHolder : Fragment() {
    private lateinit var binding : FragmentStudentGetAdmittedStepHolderBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =  FragmentStudentGetAdmittedStepHolderBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}