package com.holytrinity.users.registrar.fee_management

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.holytrinity.R
import com.holytrinity.databinding.FragmentRegistrarFeeTab2Binding


class RegistrarFeeTab2Fragment : Fragment() {
    private lateinit var binding : FragmentRegistrarFeeTab2Binding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegistrarFeeTab2Binding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }


}