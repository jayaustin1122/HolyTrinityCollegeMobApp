package com.holytrinity.users.student.admit.steps

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.holytrinity.databinding.FragmentStudentGetAdmittedStepFourBinding


class StudentGetAdmittedStepFourFragment : Fragment() {
    private lateinit var binding : FragmentStudentGetAdmittedStepFourBinding
    private lateinit var viewModel: ViewModelAdmit
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStudentGetAdmittedStepFourBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(ViewModelAdmit::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.inputUsername.addTextChangedListener {
            viewModel.userName = it.toString()
        }
        binding.inputPass.addTextChangedListener {
            viewModel.password = it.toString()
        }
        binding.confirmPass.addTextChangedListener {
            viewModel.confirmPassword = it.toString()
        }
    }
}