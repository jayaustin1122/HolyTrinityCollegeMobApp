package com.holytrinity.users.registrar.enrollment.enroll

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.lifecycle.ViewModelProvider
import com.holytrinity.R
import com.holytrinity.databinding.FragmentStepOneEnrollmentBinding


class StepOneEnrollmentFragment : Fragment() {
    private lateinit var binding : FragmentStepOneEnrollmentBinding
    private lateinit var viewModel: ViewModelEnrollment
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStepOneEnrollmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[ViewModelEnrollment::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val departmentList = listOf(viewModel.department_id,"Science", "Arts", "Commerce")
        val levelList = listOf(viewModel.level,"Freshman", "Sophomore", "Junior", "Senior")
        val courseList = listOf(viewModel.course,"Computer Science", "Business Administration", "Engineering")
        val curriculumList = listOf(viewModel.curr_id,"Curriculum A", "Curriculum B")
        val sectionList = listOf(viewModel.section,"Section 1", "Section 2", "Section 3")

        binding.studentIDTextView.text = viewModel.studentID
        binding.studentNameTextView.text = viewModel.name
        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->

            when (checkedId) {
                R.id.radio_new_student -> viewModel.classification_of_student = "New Student"
                R.id.radio_freshman -> viewModel.classification_of_student = "Freshman"
                R.id.radio_transferee -> viewModel.classification_of_student = "Transferee"
                R.id.radio_regular -> viewModel.classification_of_student = "Regular"
                R.id.radio_returnee -> viewModel.classification_of_student = "Returnee"
                R.id.radio_shifter -> viewModel.classification_of_student = "Shifter"
                R.id.radio_cross_enrollee -> viewModel.classification_of_student = "Cross Enrollee"
                R.id.radio_online -> viewModel.classification_of_student = "Online"
                R.id.radio_transnational -> viewModel.classification_of_student = "Transnational"
                R.id.radio_graduating -> viewModel.classification_of_student = "Graduating"
            }
        }

        setSpinnerSelection(binding.departmentSpinner, viewModel.department_id, departmentList)
        setSpinnerSelection(binding.levelSpinner, viewModel.level, levelList)
        setSpinnerSelection(binding.courseSpinner, viewModel.course, courseList)
        setSpinnerSelection(binding.curriculumSpinner, viewModel.curr_id, curriculumList)
        setSpinnerSelection(binding.sectionSpinner, viewModel.section, sectionList)
        setRadioButtonSelection(viewModel.classification_of_student)
    }
    private fun setRadioButtonSelection(classification: String) {
        when (classification) {
            "New Student" -> binding.radioNewStudent.isChecked = true
            "Freshman" -> binding.radioFreshman.isChecked = true
            "Transferee" -> binding.radioTransferee.isChecked = true
            "Regular" -> binding.radioRegular.isChecked = true
            "Returnee" -> binding.radioReturnee.isChecked = true
            "Shifter" -> binding.radioShifter.isChecked = true
            "Cross Enrollee" -> binding.radioCrossEnrollee.isChecked = true
            "Online" -> binding.radioOnline.isChecked = true
            "Transnational" -> binding.radioTransnational.isChecked = true
            "Graduating" -> binding.radioGraduating.isChecked = true
            else -> binding.radioGroup.clearCheck()
        }
    }

    private fun setSpinnerSelection(spinner: Spinner, value: String?, list: List<String>) {

        if (spinner.adapter == null) {
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, list)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }


        value?.let {
            val adapter = spinner.adapter
            for (i in 0 until adapter.count) {
                if (adapter.getItem(i).toString() == value) {
                    spinner.setSelection(i)
                    break
                }
            }
        }
    }



}