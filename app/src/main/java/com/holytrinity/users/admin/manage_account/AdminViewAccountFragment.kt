package com.holytrinity.users.admin.manage_account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.holytrinity.R
import com.holytrinity.databinding.FragmentAdminViewAccountBinding


class AdminViewAccountFragment : Fragment() {
    private lateinit var binding : FragmentAdminViewAccountBinding
    private var role: Int? = null
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