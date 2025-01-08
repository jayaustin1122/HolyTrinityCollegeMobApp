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
import com.holytrinity.api.ApiResponse
import com.holytrinity.api.EnrollmentService
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.databinding.FragmentStepOneEnrollmentBinding
import com.holytrinity.model.Course
import com.holytrinity.model.CourseResponse
import com.holytrinity.model.Courses
import com.holytrinity.model.EnrollmentResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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

        val levelOptions = mapOf(
            "College" to listOf("1st Year", "2nd Year", "3rd Year", "4th Year"),
            "Senior High" to listOf("Grade 11", "Grade 12")
        )


        getAllCourses()
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

        viewModel.dept_id.observe(viewLifecycleOwner) { deptId ->
            val selectedDepartment = when (deptId) {
                "1" -> "College"  // Set this based on your dept_id value
                "3" -> "Senior High"
                else -> ""
            }
            binding.departmentEditText.setText(selectedDepartment)

            // After setting the department, handle level selection accordingly
            handleDepartmentSelection(selectedDepartment, levelOptions)
        }

        viewModel.studentID.observe(viewLifecycleOwner) { studentID ->
            binding.studentIDTextView.text = studentID
            Log.d("StepOneFragment", "Updated studentID: $studentID")
        }

        viewModel.name.observe(viewLifecycleOwner) { name ->
            binding.studentNameTextView.text = name
            Log.d("StepOneFragment", "Updated name: $name")
        }

        setSpinnerSelection(binding.curriculumSpinner, semesterYearList, viewModel.curr_id.value)
        setSpinnerSelection(binding.sectionSpinner, sectionList, viewModel.section.value)


        binding.curriculumSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedSemesterYear = semesterYearList[position]
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
                    else -> ""
                }
                viewModel.setCurrId(semesterId)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        setupRadioButtons()
    }

    private fun getAllCourses() {
        val service = RetrofitInstance.create(EnrollmentService::class.java)
        service.getAllCourses()
            .enqueue(object : Callback<CourseResponse> {  // Now expecting CourseResponse
                override fun onFailure(call: Call<CourseResponse>, t: Throwable) {
                    Log.e("API_ERROR", "Failed to connect", t)
                }

                override fun onResponse(call: Call<CourseResponse>, response: Response<CourseResponse>) {
                    if (response.isSuccessful) {
                        val coursesList = response.body()?.courses ?: emptyList()  // Get the courses list
                        updateCourseSpinner(coursesList)
                    } else {
                        Log.e("API_ERROR", "Error fetching courses: ${response.message()}")
                    }
                }
            })
    }



    private fun updateCourseSpinner(courses: List<Course>) {
        if (courses.isNullOrEmpty()) {
            Log.e("StepOneEnrollmentFragment", "No courses available")
            return
        }

        val courseNames = courses.mapNotNull { it.course_name }
        val courseIds = courses.mapNotNull { it.course_id }

        if (courseNames.isEmpty() || courseIds.isEmpty()) {
            Log.e("StepOneEnrollmentFragment", "Course names or IDs are empty")
            return
        }

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            courseNames
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.courseSpinner.adapter = adapter

        binding.courseSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCourseId = courseIds.getOrNull(position)

                // Check if selectedCourseId is not null before updating the ViewModel
                if (selectedCourseId != null) {
                    viewModel.setCourseId(selectedCourseId.toString())
                } else {
                    Log.e("StepOneEnrollmentFragment", "Selected course ID is null")
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    fun handleDepartmentSelection(selectedDepartment: String, levelOptions: Map<String, List<String>>) {
        val levels = levelOptions[selectedDepartment] ?: listOf("No Levels Available")

        // Check if levels is empty and log accordingly
        if (levels.isEmpty() || levels.first() == "No Levels Available") {
            Log.e("StepOneEnrollmentFragment", "No levels available for department: $selectedDepartment")
        }

        // Create an ArrayAdapter for the level spinner
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            levels
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Temporarily clear the listener to avoid triggering it during adapter changes
        binding.levelSpinner.onItemSelectedListener = null
        binding.levelSpinner.adapter = adapter

        // Enable or disable the spinner based on available levels
        binding.levelSpinner.isEnabled = levels.isNotEmpty() && levels.first() != "No Levels Available"

        // Reset the selection and re-apply the listener
        binding.levelSpinner.setSelection(0)

        binding.levelSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedLevel = levels[position]

                // Log the selected level for debugging
                Log.d("SpinnerSelection", "Selected Level: $selectedLevel")

                // Update the ViewModel with the selected level
                viewModel.setLevel(selectedLevel)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Optionally, log if nothing is selected
                Log.d("SpinnerSelection", "No level selected")
            }
        }
    }

    private fun setupRadioButtons() {

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


}