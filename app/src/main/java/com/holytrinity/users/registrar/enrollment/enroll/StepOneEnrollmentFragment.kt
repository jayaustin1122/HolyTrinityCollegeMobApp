package com.holytrinity.users.registrar.enrollment.enroll

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.holytrinity.R
import com.holytrinity.databinding.FragmentStepOneEnrollmentBinding

class StepOneEnrollmentFragment : Fragment() {
    private lateinit var binding: FragmentStepOneEnrollmentBinding
    private lateinit var viewModel: ViewModelEnrollment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[ViewModelEnrollment::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStepOneEnrollmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val departmentList = listOf("") + listOf("College", "Pre College", "Senior High")
        val levelOptions = mapOf(
            "College" to listOf("1st Year", "2nd Year", "3rd Year", "4th Year"),
            "Senior High" to listOf("Grade 11", "Grade 12"),
            "Pre College" to emptyList()
        )
        val courseList = listOf("") + listOf(
            "BSCS - Computer Science",
            "BSIT - Information Technology",
            "BSA - Accountancy",
            "BSBA - Business Administration",
            "BSEd - Education",
            "BSCpE - Computer Engineering",
            "BSME - Mechanical Engineering",
            "BSBio - Biology",
            "BSChem - Chemistry",
            "BFA - Fine Arts"
        )
        val semesterYearList = listOf(
            "1st Semester 2024",
            "2nd Semester 2024",
            "1st Semester 2025",
            "2nd Semester 2025",
            "1st Semester 2026",
            "2nd Semester 2026",
            "1st Semester 2027",
            "2nd Semester 2027",
            "1st Semester 2028",
            "2nd Semester 2028"
        )
        val sectionList = listOf("") + listOf("A", "B")

        // Observers
        viewModel.studentID.observe(viewLifecycleOwner) { studentID ->
            binding.studentIDTextView.text = studentID
            Log.d("StepOneFragment", "Updated studentID: $studentID")
        }

        viewModel.name.observe(viewLifecycleOwner) { name ->
            binding.studentNameTextView.text = name
            Log.d("StepOneFragment", "Updated name: $name")
        }

        viewModel.dept_id.observe(viewLifecycleOwner) { deptId ->
            val selectedDepartment = when (deptId) {
                "1" -> "College"
                "2" -> "Pre College"
                "3" -> "Senior High"
                else -> ""
            }
            setSpinnerSelection(binding.departmentSpinner, departmentList, selectedDepartment)

        }

        viewModel.classification_of_student.observe(viewLifecycleOwner) { classification ->
            setRadioButtonSelection(classification)
        }
        val selectedCurriculumId = viewModel.curr_id.value
        setSpinnerSelection(binding.curriculumSpinner, semesterYearList, selectedCurriculumId)
        setSpinnerSelection(binding.departmentSpinner, departmentList, viewModel.dept_id.value)
        setSpinnerSelection(binding.courseSpinner, courseList, viewModel.course.value)
        setSpinnerSelection(binding.sectionSpinner, sectionList, viewModel.section.value)

        // Department spinner listener
        binding.departmentSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedDepartment = departmentList[position]
                viewModel.setDeptId(
                    when (selectedDepartment) {
                        "College" -> "1"
                        "Pre College" -> "2"
                        "Senior High" -> "3"
                        else -> ""
                    }
                )
                handleDepartmentSelection(selectedDepartment, levelOptions)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        binding.courseSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedCourse = courseList[position]

                // Set the course ID based on the selection
                viewModel.setCourseId(
                    when (selectedCourse) {
                        "BSCS - Computer Science" -> "1"
                        "BSIT - Information Technology" -> "2"
                        "BSA - Accountancy" -> "3"
                        "BSBA - Business Administration" -> "4"
                        "BSEd - Education" -> "5"
                        "BSCpE - Computer Engineering" -> "6"
                        "BSME - Mechanical Engineering" -> "7"
                        "BSBio - Biology" -> "8"
                        "BSChem - Chemistry" -> "9"
                        "BFA - Fine Arts" -> "10"
                        else -> ""
                    }
                )
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        binding.curriculumSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedSemesterYear = semesterYearList[position]

                // Now, set the ID to pass based on the semester and year
                val semesterId = when (selectedSemesterYear) {
                    "1st Semester 2024" -> "1"
                    "2nd Semester 2024" -> "2"
                    "1st Semester 2025" -> "3"
                    "2nd Semester 2025" -> "4"
                    "1st Semester 2026" -> "5"
                    "2nd Semester 2026" -> "6"
                    "1st Semester 2027" -> "7"
                    "2nd Semester 2027" -> "8"
                    "1st Semester 2028" -> "9"
                    "2nd Semester 2028" -> "10"
                    else -> ""  // Default case
                }

                // Assuming there's a method to pass the semester ID to your view model
                viewModel.setCurrId(semesterId)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }


        // Level spinner listener
        binding.levelSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (binding.levelSpinner.isEnabled) {
                    val selectedLevel = parent?.getItemAtPosition(position) as? String ?: ""
                    if (selectedLevel != "No Levels Available" && selectedLevel.isNotEmpty()) {
                        viewModel.setLevel(selectedLevel)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        setupRadioButtons()
    }

    private fun handleDepartmentSelection(selectedDepartment: String, levelOptions: Map<String, List<String>>) {
        val levelList = levelOptions[selectedDepartment] ?: emptyList()

        if (levelList.isNotEmpty()) {
            // If we have levels, set them and enable the spinner
            setSpinnerSelection(binding.levelSpinner, levelList, viewModel.level.value)
            binding.levelSpinner.isEnabled = true
        } else {
            // No levels for this department
            setSpinnerSelection(binding.levelSpinner, listOf("No Levels Available"), null)
            binding.levelSpinner.isEnabled = false
        }
    }

    private fun setupRadioButtons() {
        // By default, RadioGroup ensures only one button can be selected at a time.
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val classification = when (checkedId) {
                R.id.radio_new_student -> "New Student"
                R.id.radio_freshman -> "Freshman"
                R.id.radio_transferee -> "Transferee"
                R.id.radio_regular -> "Regular"
                R.id.radio_returnee -> "Returnee"
                R.id.radio_shifter -> "Shifter"
                R.id.radio_cross_enrollee -> "Cross Enrollee"
                R.id.radio_online -> "Online"
                R.id.radio_transnational -> "Transnational"
                R.id.radio_graduating -> "Graduating"
                else -> ""
            }
            viewModel.setClassificationOfStudent(classification)
        }
    }

    // Helper function to set spinner selection
    private fun setSpinnerSelection(spinner: Spinner, options: List<String>, selectedOption: String?) {
        val adapter = ArrayAdapter(spinner.context, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        selectedOption?.let {
            val position = options.indexOf(it)
            if (position >= 0) {
                spinner.setSelection(position)
            }
        }
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
}