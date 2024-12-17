package com.holytrinity.users.cashier.payment_management

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.holytrinity.R
import com.holytrinity.databinding.FragmentCashierPaymentManagementBinding


class CashierPaymentManagementFragment : Fragment() {
    private lateinit var binding : FragmentCashierPaymentManagementBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCashierPaymentManagementBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
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