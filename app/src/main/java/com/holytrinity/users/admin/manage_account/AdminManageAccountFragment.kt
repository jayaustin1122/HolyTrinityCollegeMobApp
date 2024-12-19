package com.holytrinity.users.admin.manage_account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.holytrinity.R
import com.holytrinity.databinding.FragmentAdminManageAccountBinding

class AdminManageAccountFragment : Fragment() {
    private lateinit var binding: FragmentAdminManageAccountBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminManageAccountBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbarBackButton.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("selectedFragmentId", null ?: R.id.nav_dashboard_admin)
            }
            findNavController().navigate(R.id.adminDrawerFragment, bundle)
        }
    }

}