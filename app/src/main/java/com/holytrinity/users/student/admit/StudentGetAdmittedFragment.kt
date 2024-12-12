package com.holytrinity.users.student.admit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.holytrinity.R
import com.holytrinity.databinding.FragmentStudentGetAdmittedBinding


class StudentGetAdmittedFragment : Fragment() {
    private lateinit var binding : FragmentStudentGetAdmittedBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =  FragmentStudentGetAdmittedBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.radioCollege.setOnClickListener {

        }
        binding.radioSeniorHigh.setOnClickListener {

        }
        binding.btnContinue.setOnClickListener {
            findNavController()
        }
    }
}