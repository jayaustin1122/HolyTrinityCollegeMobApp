package com.holytrinity.users.student.admit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.holytrinity.R
import com.holytrinity.databinding.FragmentStudentGetAdmittedBinding
import com.holytrinity.users.registrar.enrollment.enroll.ViewModelEnrollment
import com.holytrinity.users.student.admit.steps.ViewModelAdmit


class StudentGetAdmittedFragment : Fragment() {
    private lateinit var binding : FragmentStudentGetAdmittedBinding
    private lateinit var viewModel: ViewModelAdmit
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =  FragmentStudentGetAdmittedBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[ViewModelAdmit::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnContinue.setOnClickListener {
            val selectedOption = when {
                binding.radioCollege.isChecked -> "College"
                binding.radioSeniorHigh.isChecked -> "Senior High"
                else -> null
            }
            if (selectedOption != null) {
                viewModel.userType = selectedOption
                findNavController().navigate(R.id.studentGetAdmittedStepHolder)
            } else {
                Toast.makeText(requireContext(), "Please select an option before proceeding.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}