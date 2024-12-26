package com.holytrinity.users.registrar.admisson

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.holytrinity.R
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.api.StudentService
import com.holytrinity.databinding.FragmentRegistrarAdmissionBinding
import com.holytrinity.model.Student
import com.holytrinity.users.registrar.adapter.RegistrarAdmissionAdapter
import com.holytrinity.util.UserPreferences
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrarAdmissionFragment : Fragment() {

    private lateinit var binding: FragmentRegistrarAdmissionBinding
    private var filteredStudents: List<Student> = emptyList()
    private lateinit var studentsAdapter: RegistrarAdmissionAdapter
    private var students: List<Student> = emptyList()
    private lateinit var studentNamesMap: MutableMap<String, String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegistrarAdmissionBinding.inflate(layoutInflater)
        return binding.root
    }

    // --- onViewCreated ---
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup adapter
        studentsAdapter = RegistrarAdmissionAdapter(filteredStudents) { studentId ->
            val bundle = Bundle().apply {
                putString("studentId", studentId)
            }
            findNavController().navigate(R.id.registrarAdmissionDetailsFragment, bundle)
        }

        // Back button
        val roleId = UserPreferences.getRoleId(requireContext())
        binding.toolbarBackButton.setOnClickListener {
            navigateBasedOnRole(roleId)
        }

        // Recycler
        binding.recyclerEnrolledSubjects.layoutManager = LinearLayoutManager(context)
        binding.recyclerEnrolledSubjects.adapter = studentsAdapter

        // Search
        binding.useridTextView.addTextChangedListener {
            val query = it.toString().trim()
            filterStudents(query)
        }

        // 1) By default, fetch all students (no registrationVerified filter)
        getAllStudents()

        // 2) Open bottom sheet filter on button click
        binding.btnFilter.setOnClickListener {
            val bottomSheet = BottomSheetFilterAdmissionFragment().apply {
                // We set a listener or pass a callback to get the chosen filter
                onFilterApplied = { regVerified ->
                    // This is called when user taps "Apply" in the bottom sheet
                    applyFilter(regVerified)
                }
            }
            bottomSheet.show(childFragmentManager, "BottomSheetFilterAdmissionFragment")
        }
    }

    // --- Called by bottom sheet once user picks 0/1/2 ---
    private fun applyFilter(registrationVerified: String) {
        // Call getAllStudents with the selected filter
        getAllStudents(registrationVerified = registrationVerified)
    }

    // --- Get all students (optionally filtered by registrationVerified) ---
    private fun getAllStudents(studentId: String? = null, registrationVerified: String? = null) {
        val studentService = RetrofitInstance.create(StudentService::class.java)

        // We'll create a custom method in StudentService to pass registrationVerified
        // But if you only have getStudents() returning all, you can filter in client side
        // Example: studentService.getAllStudents(registrationVerified) <-- if your API supports it

        studentService.getStudents().enqueue(object :
            Callback<List<Student>> {
            override fun onResponse(
                call: Call<List<Student>>,
                response: Response<List<Student>>
            ) {
                if (response.isSuccessful) {
                    students = response.body() ?: emptyList()

                    // If user selected a filter (0,1,2) but your API doesn't support it,
                    // you can do client-side filtering:
                    val filtered = if (registrationVerified != null) {
                        // only keep students whose registration_verified == registrationVerified
                        students.filter {
                            it.registration_verified?.toString() == registrationVerified
                        }
                    } else {
                        students
                    }

                    filteredStudents = filtered
                    studentsAdapter = RegistrarAdmissionAdapter(filteredStudents) { studId ->
                        val bundle = Bundle().apply {
                            putString("studentId", studId)
                        }
                        findNavController().navigate(R.id.registrarAdmissionDetailsFragment, bundle)
                    }

                    binding.recyclerEnrolledSubjects.adapter = studentsAdapter
                    studentsAdapter.notifyDataSetChanged()

                } else {
                    Log.e("Error", "Failed to fetch students: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<Student>>, t: Throwable) {
                Log.e("Error", "Failed to fetch students: ${t.message}")
                Toast.makeText(requireContext(), "Failed to fetch students", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // --- Local search ---
    private fun filterStudents(query: String) {
        filteredStudents = if (query.isEmpty()) {
            students
        } else {
            students.filter { it.student_name?.contains(query, ignoreCase = true) == true }
        }
        studentsAdapter.updateData(filteredStudents)
    }

    private fun navigateBasedOnRole(roleId: Int) {
        when (roleId) {
            1 -> findNavController().navigate(R.id.adminDrawerFragment)
            2 -> findNavController().navigate(R.id.registrarDrawerHolderFragment)
            4 -> findNavController().navigate(R.id.cashierDrawerFragment)
            5 -> findNavController().navigate(R.id.instructorDrawerHolderFragment)
            6 -> findNavController().navigate(R.id.parentDrawerHolderFragment)
            7 -> findNavController().navigate(R.id.studentDrawerHolderFragment)
            10 -> findNavController().navigate(R.id.benefactorDrawerHolderFragment)
            else -> {
                // Default navigation if roleId doesn't match any of the above
                Toast.makeText(
                    requireContext(),
                    "Invalid role, navigating back to dashboard",
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().navigate(R.id.nav_dashboard) // You can replace this with a default fragment
            }
        }
    }
}
