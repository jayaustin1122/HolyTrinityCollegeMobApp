package com.holytrinity.users.admin.scheduling

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.canorecoapp.utils.DialogUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.holytrinity.api.ClassesService
import com.holytrinity.api.InsertClassService
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.api.SubjectService
import com.holytrinity.api.UserLoginService
import com.holytrinity.databinding.FragmentBottomSheetAddScheduleBinding
import com.holytrinity.model.InsertClassResponse
import com.holytrinity.model.Subject
import com.holytrinity.model.Instructors
import com.holytrinity.model.InstructorsResponse
import com.holytrinity.model.SuggestedSchedule
import com.holytrinity.util.SharedPrefsUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class BottomSheetAddScheduleFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentBottomSheetAddScheduleBinding
    private var selectedSection: String? = null
    private var selectedSubject: Subject? = null
    private var selectedSubject_id: Int = 0
    private var selectedSubject_hrs: Int = 0
    private var selectedInstructor: Instructors? = null
    private var selectedInstructor_id: Int = 0
    private var sched: String = ""
    private var sectionSect: String = ""
    private lateinit var loadingDialog: SweetAlertDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBottomSheetAddScheduleBinding.inflate(layoutInflater)
        return binding.root
    }

    companion object {
        const val TAG = "BottomSheetAddEventFragment"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up section spinner
        val sectionList = listOf("A", "B")
        setSpinnerSelection(binding.sectionSpinner, sectionList)

        binding.sectionSpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    sectionSect = parentView?.getItemAtPosition(position).toString()
                    Log.d("SelectedSection", "Selected Section: $sectionSect")
                    getAllSubjects(sectionSect)
                }

                override fun onNothingSelected(parentView: AdapterView<*>?) {
                    // You can set a default value or handle when nothing is selected
                }
            }




        binding.doneButton.setOnClickListener {
            val savedPeriod = SharedPrefsUtil.getSelectedPeriod(requireContext())
            val enrollmentPeriod = savedPeriod!!.enrollment_period_id
            loadingDialog = DialogUtils.showLoading(requireActivity())
            loadingDialog.show()

            uploadDB(
                selectedSubject_id,
                selectedInstructor_id,
                enrollmentPeriod,
                sched,
                sectionSect,
                30
            )
        }
    }

    private fun uploadDB(
        subjectId: Int,
        instructorUserId: Int,
        enrollmentPeriodId: Int?,
        schedule: String,
        section: String,
        maxEnrollment: Int
    ) {

        if (enrollmentPeriodId == null || enrollmentPeriodId == 0) {
            Toast.makeText(
                requireContext(),
                "Please go to setup and select a semester or period.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        Log.d(
            "UploadDB", "Uploading: section=$section, schedule=$schedule, " +
                    "instructor=$instructorUserId, subject=$subjectId, maxEnroll=$maxEnrollment"
        )

        val service = RetrofitInstance.create(InsertClassService::class.java)

        service.insertInClasses(
            subjectId = subjectId,
            instructorUserId = instructorUserId,
            enrollmentPeriodId = enrollmentPeriodId,
            schedule = schedule,
            section = section,
            maxEnrollment = maxEnrollment
        ).enqueue(object : Callback<InsertClassResponse> {
            override fun onResponse(
                call: Call<InsertClassResponse>,
                response: retrofit2.Response<InsertClassResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val resp = response.body()!!
                    if (resp.status == "success") {
                        Log.d("UploadDB", "Class inserted successfully: ${resp.message}")
                        loadingDialog.dismiss()
                        Toast.makeText(
                            requireContext(),
                            "Class Schedule inserted successfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        dismiss()
                    } else {
                        loadingDialog.dismiss()
                        Log.e("UploadDB", "Error inserting class: ${resp.error}")
                    }
                } else {
                    loadingDialog.dismiss()
                    Log.e("UploadDB", "Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<InsertClassResponse>, t: Throwable) {
                loadingDialog.dismiss()
                Log.e("UploadDB", "Failed to insert class", t)
            }
        })
    }
    private fun getAllSubjects(sectionSect: String) {
        val service = RetrofitInstance.create(SubjectService::class.java)
        service.getAllSubjects().enqueue(object : Callback<List<Subject>> {
            override fun onResponse(call: Call<List<Subject>>, response: Response<List<Subject>>) {
                if (response.isSuccessful) {
                    val subjects = response.body() ?: emptyList()
                    val subjectNames = subjects.map { it.name }
                    setSpinnerSelection(binding.subjectSpinner, subjectNames)

                    // When a subject is selected, fetch the instructors
                    binding.subjectSpinner.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parentView: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                selectedSubject = subjects[position]
                                selectedSubject_id = subjects[position].subject_id!!
                                selectedSubject_hrs = subjects[position].required_hrs!!

                                // Fetch instructors based on the selected subject
                                getAllInstructors(sectionSect)
                            }

                            override fun onNothingSelected(parentView: AdapterView<*>?) {}
                        }
                } else {
                    Log.e("API_ERROR", "Error fetching subjects: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Subject>>, t: Throwable) {
                Log.e("API_ERROR", "Failed to connect for subjects", t)
            }
        })
    }

    private fun getAllInstructors(sectionSect: String) {
        val service = RetrofitInstance.create(UserLoginService::class.java)
        service.getAllInstructors(sectionSect).enqueue(object : Callback<InstructorsResponse> {
            override fun onResponse(
                call: Call<InstructorsResponse>,
                response: Response<InstructorsResponse>
            ) {
                if (response.isSuccessful) {
                    val instructorsResponse = response.body()
                    val instructors = instructorsResponse?.instructors ?: emptyList()

                    // Log the instructor names to see if the data is correct
                    val instructorNames = instructors.map { it.name }
                    Log.d("Instructor Names", instructorNames.toString())

                    // Check if instructorNames is empty
                    if (instructorNames.isEmpty()) {
                        Log.e("Instructor Data", "No instructor names found!")
                        return
                    }

                    // Set the spinner with instructor names
                    val adapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_spinner_item,
                        instructorNames
                    )
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    binding.instructorSpinner.adapter = adapter

                    // When an instructor is selected, refresh the schedule
                    binding.instructorSpinner.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parentView: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                selectedInstructor = instructors[position]
                                selectedInstructor_id = instructors[position].user_id.toInt()

                                // Fetch the available schedule based on the selected subject and instructor
                                getAllClassesWithSched(selectedInstructor_id, selectedSubject_hrs)
                            }

                            override fun onNothingSelected(parentView: AdapterView<*>?) {}
                        }
                } else {
                    Log.e("API_ERROR", "Error fetching instructors: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<InstructorsResponse>, t: Throwable) {
                Log.e("API_ERROR", "Failed to connect for instructors", t)
            }
        })
    }

    private fun getAllClassesWithSched(selectedInstructor_id: Int, selectedSubject_hrs: Int) {
        val service = RetrofitInstance.create(ClassesService::class.java)
        service.getAllClassesWithSched(selectedInstructor_id, selectedSubject_hrs)
            .enqueue(object : Callback<SuggestedSchedule> {
                override fun onResponse(
                    call: Call<SuggestedSchedule>,
                    response: Response<SuggestedSchedule>
                ) {
                    if (response.isSuccessful) {
                        val suggestedSchedule = response.body()
                        suggestedSchedule?.suggested_schedules?.let { schedules ->
                            val scheduleStrings = schedules.map { "${it.day} ${it.time}" }
                            setSpinnerSelection(binding.schedSpinner, scheduleStrings)

                            binding.schedSpinner.onItemSelectedListener =
                                object : AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(
                                        parentView: AdapterView<*>?,
                                        view: View?,
                                        position: Int,
                                        id: Long
                                    ) {
                                        sched = scheduleStrings[position]
                                        Log.d("SelectedSchedule", "Selected Schedule: $sched")
                                    }

                                    override fun onNothingSelected(parentView: AdapterView<*>?) {}
                                }
                        } ?: run {
                            Log.e("API_ERROR", "No suggested schedules available")
                        }
                    } else {
                        Log.e("API_ERROR", "Error fetching classes: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<SuggestedSchedule>, t: Throwable) {
                    Log.e("API_FAILURE", "Error fetching classes", t)
                }
            })
    }
    private fun setSpinnerSelection(spinner: Spinner, options: List<String>) {
        val adapter = ArrayAdapter(spinner.context, android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }
}
