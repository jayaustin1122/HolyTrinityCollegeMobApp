package com.holytrinity.users.registrar.admisson

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.holytrinity.R
import com.holytrinity.databinding.FragmentRegistrarAdmissionBinding

class RegistrarAdmissionFragment : Fragment() {

    private lateinit var binding: FragmentRegistrarAdmissionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegistrarAdmissionBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbarBackButton.setOnClickListener {
            val bundle = Bundle().apply {
                putInt("selectedFragmentId", null ?: R.id.nav_dashboard)
            }
            findNavController().navigate(R.id.registrarDrawerHolderFragment, bundle)
        }
    }
}