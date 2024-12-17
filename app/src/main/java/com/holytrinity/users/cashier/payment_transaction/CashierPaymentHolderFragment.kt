package com.holytrinity.users.cashier.payment_transaction

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.canorecoapp.utils.DialogUtils
import com.holytrinity.R
import com.holytrinity.databinding.FragmentCashierPaymentHolderBinding

class CashierPaymentHolderFragment : Fragment() {
    private lateinit var binding : FragmentCashierPaymentHolderBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCashierPaymentHolderBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbarBackButton.setOnClickListener {
            DialogUtils.showWarningMessage(requireActivity(), "Confirm Exit", "Click \"Yes\" to cancel discard any changes made."
            ) { sweetAlertDialog ->
                sweetAlertDialog.dismissWithAnimation()

                val bundle = Bundle().apply {
                    putInt("selectedFragmentId", null ?: R.id.nav_dashboard)
                }
                findNavController().navigate(R.id.registrarDrawerHolderFragment, bundle)
            }
        }
    }
}