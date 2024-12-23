package com.holytrinity.users.cashier.payment_transaction

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.canorecoapp.utils.DialogUtils
import com.holytrinity.R
import com.holytrinity.databinding.FragmentCashierPaymentHolderBinding
import com.holytrinity.model.Payment
import com.holytrinity.util.BluetoothHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CashierPaymentHolderFragment : Fragment() {

    private lateinit var binding : FragmentCashierPaymentHolderBinding
    private lateinit var bluetoothFn: BluetoothHelper

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
                    putInt("selectedFragmentId", null ?: R.id.nav_dashboard_cashier)
                }
                findNavController().navigate(R.id.cashierDrawerFragment, bundle)
            }
        }

// Onclick for printing Receipt
//        binding.nextButton.setOnClickListener {
//            val message = generateReceiptMessage(
//                date, time, studentId, studentName, itemsToPay, totalAmount, cashAmount, changeAmount
//            )
//
//            CoroutineScope(Dispatchers.IO).launch {
//                val isPrinted = bluetoothFn.printTextFeed(message)
//                withContext(Dispatchers.Main) {
//                    if (isPrinted) {
//                        Toast.makeText(requireContext(), "Printed Successfully", Toast.LENGTH_SHORT)
//                            .show()
//                    } else {
//                        Toast.makeText(requireContext(), "Printing Failed", Toast.LENGTH_SHORT)
//                            .show()
//                    }
//                }
//            }
//        }
    }

    fun generateReceiptMessage(
        date: String,
        time: String,
        studentId: String,
        studentName: String,
        itemsToPay: List<Payment>,
        totalAmount: String,
        cashAmount: String,
        changeAmount: String
    ): String {
        return StringBuilder().apply {
            append("\n\n\n\nRoman Catholic Bishop of Daet\n")
            append("\u001B\u0045\u0001") // Bold ON
            append("HOLY TRINITY COLLEGE SEMINARY\n")
            append("       FOUNDATION, Inc.      \n")
            append("\u001B\u0045\u0000") // Bold OFF
            append("Holy Trinity College Seminary\n")
            append("   P.3 Bautista 4604, Labo   \n")
            append("Camarines Norte, Philippines \n")
            append("=============================\n")
            append("ID:   $studentId  DATE: $date\n")
            append("Name: $studentName   TIME: $time\n")
            append("=============================\n")
            append("\u001B\u0045\u0001") // Bold ON
            append("Description         Amount   \n")
            append("\u001B\u0045\u0000") // Bold OFF
            // Add items dynamically
            itemsToPay.forEach { item ->
                append("${item.description.padEnd(17)} ${item.amount.padStart(10)}\n")
            }
            append("=============================\n")
            append("\u001B\u0045\u0001") // Bold ON
            append("Total              $totalAmount   \n")
            append("\u001B\u0045\u0000") // Bold OFF
            append("Cash               $cashAmount   \n")
            append("Change             $changeAmount   \n")
            append("=============================\n\n")
            append("\u001B\u0045\u0001") // Bold ON
            append("          Thank You!         \n\n\n\n")
            append("\u001B\u0045\u0000") // Bold OFF
        }.toString()
    }
}