package com.holytrinity.users.admin.manage_account

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.holytrinity.R
import com.holytrinity.api.RetrofitInstance
import com.holytrinity.api.StudentService
import com.holytrinity.databinding.FragmentAdminViewAccountBinding
import com.holytrinity.model.Student
import com.holytrinity.users.admin.adapter.StudentAdapter
import com.holytrinity.users.registrar.adapter.SoaAdapter
import com.holytrinity.util.UserPreferences
import retrofit2.Call
import retrofit2.Callback


class AdminViewAccountFragment : Fragment() {
    private lateinit var binding : FragmentAdminViewAccountBinding
    private var role: Int? = null
    private lateinit var studentAdapter: StudentAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminViewAccountBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userId = arguments?.getInt("userId")
         role = arguments?.getInt("role")
        val name = arguments?.getString("name")
        binding.studentName.text = name
        binding.studentNumber.text = userId.toString()
        binding.fabAddEvent.setOnClickListener {
            showStudentSelector(userId!!)
        }
        val fromRole = when (role) {
            10 -> "Benefactor"
            6 -> "Parent"
            else -> "Unknown"
        }

        getStudentsInBeneParent(fromRole, userId)

        binding.toolbarBackButton.setOnClickListener {
            findNavController().apply {
                navigate(R.id.adminManageAccountFragment)
            }
        }
    }

    private fun getStudentsInBeneParent(fromRole: String, userId: Int?) {
        val studentService = RetrofitInstance.create(StudentService::class.java)
        val call = studentService.getStudentsInBeneParent(userId, fromRole)
        call.enqueue(object : Callback<List<Student>> {
            override fun onResponse(call: Call<List<Student>>, response: retrofit2.Response<List<Student>>) {
                if (response.isSuccessful) {
                    val students = response.body() ?: emptyList()
                    binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
                    studentAdapter = StudentAdapter(students)
                    binding.recyclerView.adapter = studentAdapter
                } else {
                    response.errorBody()?.let {
                        Log.e("StudentSelector", "Error Response: ${it.string()}")
                    }
                }
            }

            override fun onFailure(call: Call<List<Student>>, t: Throwable) {
                Log.e("StudentSelector", "Failed to add student: ${t.message}")
            }
        })
    }


    private fun showStudentSelector(userId: Int) {
        val bottomSheet = StudentSelectorBottomSheet()
        val args = Bundle()
        args.putInt("user_id", userId)
        args.putInt("role", role!!)
        bottomSheet.arguments = args
        bottomSheet.show(parentFragmentManager, bottomSheet.tag)
    }
}