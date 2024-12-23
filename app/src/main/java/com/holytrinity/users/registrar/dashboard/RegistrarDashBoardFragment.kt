package com.holytrinity.users.registrar.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.holytrinity.databinding.FragmentRegistrarDashBoardBinding
import com.holytrinity.util.UserPreferences


class RegistrarDashBoardFragment : Fragment() {
    private lateinit var binding : FragmentRegistrarDashBoardBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegistrarDashBoardBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textViewUser.text = UserPreferences.getName(requireContext())
    }
}